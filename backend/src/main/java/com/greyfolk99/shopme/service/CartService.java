package com.greyfolk99.shopme.service;

import com.greyfolk99.shopme.domain.cart.Cart;
import com.greyfolk99.shopme.domain.cart.CartItem;
import com.greyfolk99.shopme.domain.item.Item;
import com.greyfolk99.shopme.domain.member.Member;
import com.greyfolk99.shopme.dto.request.CartItemRequest;
import com.greyfolk99.shopme.dto.request.OrderItemDetailRequest;
import com.greyfolk99.shopme.dto.request.OrderItemRequest;
import com.greyfolk99.shopme.dto.response.CartListResponse;
import com.greyfolk99.shopme.exception.ExceptionClass;
import com.greyfolk99.shopme.exception.rest.*;
import com.greyfolk99.shopme.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ItemRepository itemRepository;
    private final OrderService orderService;
    private final MemberService memberService;

    // Cart 존재하면 가져오고 없으면 새로 만들어서 저장
    public Cart getCartByMember(Member member){
        return cartRepository.findByMemberUuid(member.getUuid())
            .orElseGet(() -> cartRepository.save(Cart.of(member)));
    }

    // 회원 장바구니에 상품 추가
    public Long addCartItem(CartItemRequest cartItemRequest, Member member) {
        Cart cart = getCartByMember(member);
        // Item 영속
        Item item = itemRepository.findById(cartItemRequest.getItemId())
            .orElseThrow(() -> new ResourceNotFoundException(ExceptionClass.ITEM, "상품을 찾을 수 없습니다."));
        // 재고 검증
        checkStock(item, cartItemRequest.getCount());
        // CartItem 없으면 생성, 있으면 수량 추가
        CartItem cartItem = cartItemRepository.findByCartIdAndItemId(cart.getId(), item.getId())
            .orElseGet(() -> cartItemRepository.save(CartItem.of(cart, item, cartItemRequest.getCount())));
        if (cartItem.getCount() != cartItemRequest.getCount()) cartItem.addCount(cartItemRequest.getCount());
        return cartItem.getId();
    }

    public void checkStock(Item item, int count) {
        int currentStock = item.getStock();
        if(currentStock < count) throw new OutOfStockException("수량이 부족합니다."+"남은 상품 수량은 "+ currentStock+"개 입니다.");
    }

    // 회원 장바구니 상품 목록 조회 (기존 쿠키에 있는 상품과 DB에 있는 상품 합쳐서 DB에 저장)
    public Cart mergeCart(Collection<CartItemRequest> cookieCartItems, Member member) {
        Cart cart = getCartByMember(member);
        List<CartItem> cartItems = cart.getCartItems();
        Map<Item, CartItem> cartItemMap = cartItems.stream()
            .collect(Collectors.toMap(CartItem::getItem, Function.identity()));
        cookieCartItems.forEach(cookieCartItem -> {
            Item item = itemRepository.findById(cookieCartItem.getItemId())
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionClass.ITEM, "상품을 찾을 수 없습니다."));
            CartItem cartItem = cartItemMap.get(item);
            if (cartItem == null) {
                cartItem = cartItemRepository.save(CartItem.of(cart, item, cookieCartItem.getCount()));
                cartItems.add(cartItem);
            } else {
                cartItem.addCount(cookieCartItem.getCount());
            }
        });
        return cart;
    }

    // 회원 장바구니 상품 수 갱신
    public Long updateCartItemCount(CartItemRequest cartItemRequest, Member member){
        Cart cart = getCartByMember(member);
        CartItem cartItem = cartItemRepository.findByCartIdAndItemId(cart.getId(), cartItemRequest.getItemId())
            .orElseThrow(() -> new ResourceNotFoundException(ExceptionClass.CART_ITEM, "장바구니 상품을 찾을 수 없습니다."));
        cartItem.updateCount(cartItemRequest.getCount()); // auto commit
        return cartItem.getId();
    }

    // 회원 장바구니 상품 제거
    public void deleteCartItem(Long itemId, Member member) {
        CartItem cartItem = cartItemRepository.findByCartIdAndItemId(getCartByMember(member).getId(), itemId)
            .orElseThrow(() -> new ResourceNotFoundException(ExceptionClass.CART_ITEM, "장바구니 상품을 찾을 수 없습니다."));
        cartItemRepository.delete(cartItem);
    }

    // 주문 생성
    public Long orderCartItem(List<OrderItemRequest> orderItemRequests, Member member) {

        Cart cart = getCartByMember(member);
        List<CartItem> cartItems = cartItemRepository.findCartItemsJoinFetchItemAndCartByCartId(cart.getId());
        Set<OrderItemDetailRequest> orderItemDetailRequests = orderItemRequests.stream()
            .map(OrderItemDetailRequest::of)
            .collect(Collectors.toSet());

        if (!validateCartItemsForOrder(cartItems, orderItemDetailRequests)) {
            throw new InternalServerException(ExceptionClass.CART_ITEM, "요청 처리에 실패했습니다.");
        }
        Long orderId = orderService.order(orderItemDetailRequests, member);
        cartItems.forEach(cartItem -> cartItemRepository.deleteById(cartItem.getId()));
        return orderId;
    }

    public boolean validateCartItemsForOrder(Collection<CartItem> cartItems, Collection<OrderItemDetailRequest> orderItemRequests) {
        Set<OrderItemDetailRequest> itemsFromRequest = new HashSet<>(orderItemRequests);
        Set<OrderItemDetailRequest> itemsFromCart = cartItems.stream()
            .map(cartItem -> OrderItemDetailRequest.of(cartItem.getItem(), cartItem.getCount()))
            .collect(Collectors.toSet());
        return itemsFromRequest.containsAll(itemsFromCart);
    }

    public List<CartListResponse> getCartList(Collection<CartItemRequest> cookieCartItems) {
        return cookieCartItems.stream()
            .map(cookieCartItem -> CartItem.ofAnonymous(
                itemRepository.getReferenceById(cookieCartItem.getItemId()), cookieCartItem.getCount()))
            .map(CartItem::toResponse)
            .toList();
    }

    public List<CartListResponse> getCartList(Cart cart) {
        return cartItemRepository.findCartListResponse(cart.getId());
    }
}
