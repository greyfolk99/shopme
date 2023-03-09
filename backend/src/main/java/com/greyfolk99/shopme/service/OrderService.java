package com.greyfolk99.shopme.service;


import com.greyfolk99.shopme.domain.item.Item;
import com.greyfolk99.shopme.domain.member.Member;
import com.greyfolk99.shopme.domain.order.Order;
import com.greyfolk99.shopme.domain.order.OrderItem;
import com.greyfolk99.shopme.dto.request.OrderItemDetailRequest;
import com.greyfolk99.shopme.dto.response.OrderHistoryResponse;
import com.greyfolk99.shopme.exception.ExceptionClass;
import com.greyfolk99.shopme.exception.rest.InternalServerException;
import com.greyfolk99.shopme.exception.rest.ResourceNotFoundException;
import com.greyfolk99.shopme.repository.ItemImageRepository;
import com.greyfolk99.shopme.repository.ItemRepository;
import com.greyfolk99.shopme.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.List;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final ItemRepository itemRepository;
    private final MemberService memberService;
    private final OrderRepository orderRepository;
    private final ItemImageRepository itemImageRepository;

    //  repetitive methods
    private Item findItemById(Long id){
        return itemRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException(
            ExceptionClass.ITEM, HttpStatus.NOT_FOUND, "없는 상품입니다."));}
    private Order findOrderById(Long id){
        return orderRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException(
            ExceptionClass.ORDER, HttpStatus.NOT_FOUND, "등록된 주문이 아닙니다."));}

    // List<OrderItemRequest> 로 주문
    @Transactional(rollbackFor = Exception.class)
    public Long order(final Set<OrderItemDetailRequest> orderItemDto, final Member member) {
        // Order 객체 생성
        final Order order = Order.of(member);
        // 주문
        orderItemDto.forEach(dto -> {
            // Item 영속
            Item item = findItemById(dto.getItemId());
            // * 주문 프로세스 (예: 결제나 배송 관련 로직이 연계되는 부분)
            boolean isSuccess;
            isSuccess = true;
            if (isSuccess) {
                order.confirm();
                // Item 재고 업데이트 *Throwable OutOfStockException
                item.sold(dto.getCount());
                // OrderItem 생성 후 Order 객체에 추가
                OrderItem orderItem = OrderItem.of(order, item, dto.getCount());
                order.addOrderItem(orderItem);
            }
        });
        // Order, OrderItem 같이 저장(CASCADE 옵션으로 같이 저장)
        Order savedOrder = orderRepository.save(order);

        return savedOrder.getId();
    }

    // 주문 내역 구하기
    @Transactional(readOnly = true)
    public Page<OrderHistoryResponse> getOrderList(final Member member, Pageable pageable) {

        // 주문 영속
        List<Order> orders = orderRepository.findOrders(member.getUsername());
        Long totalCount = orderRepository.countOrder(member.getUsername());

        // OrderHistoryResponse 생성
        List<OrderHistoryResponse> orderHistoryResponseList = orders.stream()
            .map(order -> {
                OrderHistoryResponse orderHistoryResponse = OrderHistoryResponse.from(order);
                orderHistoryResponse.addOrderItems(orderRepository.getOrderItemResponses(order));
                return orderHistoryResponse; })
            .toList();

        return new PageImpl<>(orderHistoryResponseList, pageable, totalCount);
    }

    //  Principal의 Username 검증
    @Transactional(readOnly = true)
    public Order validateOrder(Long orderId, Member member) {
        Order order = findOrderById(orderId);
        System.out.println("member.getUuid() = " + member.getUuid());
        System.out.println("member.getUuid() = " + order.getMember().getUuid());
        if (!StringUtils.equals(member.getUuid(), order.getMember().getUuid()))
            throw new InternalServerException(ExceptionClass.MEMBER, HttpStatus.UNAUTHORIZED, "잘못된 요청입니다.");
        return order;
    }

    // 주문 취소
    public void cancel(Order order) {
        order.cancel();
    }
}
