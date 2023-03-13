package com.greyfolk99.shopme.controller;

import com.greyfolk99.shopme.domain.member.Member;
import com.greyfolk99.shopme.domain.order.Order;
import com.greyfolk99.shopme.dto.request.OrderItemDetailRequest;
import com.greyfolk99.shopme.dto.request.OrderItemRequest;
import com.greyfolk99.shopme.dto.response.OrderHistoryResponse;
import com.greyfolk99.shopme.exception.ExceptionClass;
import com.greyfolk99.shopme.exception.rest.ValidationFailedException;
import com.greyfolk99.shopme.service.CartService;
import com.greyfolk99.shopme.service.MemberService;
import com.greyfolk99.shopme.service.OrderService;
import com.greyfolk99.shopme.service.PaginationService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.security.Principal;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final CartService cartService;
    private final MemberService memberService;
    private final PaginationService paginationService;

    // 상품 바로 주문
    @ApiOperation(value = "상품 바로 주문", notes = "상품 바로 주문")
    @PostMapping(value = "/order/api/item") @ResponseBody
    public ResponseEntity<?> orderFromItemDetail(
            @RequestBody @Valid OrderItemRequest orderItemRequest,
            BindingResult bindingResult,
            Principal principal
    ) {
        if (bindingResult.hasErrors()) {
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                sb.append(fieldError.getDefaultMessage());
            }
            throw new ValidationFailedException(ExceptionClass.ITEM, sb.toString());
        }

        Member member = (Member) memberService.loadUserByUsername(principal.getName());

        OrderItemDetailRequest orderItemDetailRequest = OrderItemDetailRequest.of(orderItemRequest);
        Long orderId = orderService.order(Set.of(orderItemDetailRequest), member);

        Map<String, String> body = new HashMap<>();
        body.put("message", "주문이 완료되었습니다.");
        return ResponseEntity.ok(body);
    }

    @ApiOperation(value = "장바구니에서 주문하기", notes = "장바구니에서 주문하기")
    @PostMapping(value = "/order/api/cart") @ResponseBody
    public ResponseEntity<?> orderFromCart(
            Principal principal,
            @RequestBody @Valid List<OrderItemRequest> orderItemRequests,
            BindingResult bindingResult
    ) {
        // Validation
        if (bindingResult.hasErrors()) {
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                sb.append(fieldError.getDefaultMessage());
            }
            throw new ValidationFailedException(ExceptionClass.ITEM, sb.toString());
        }
        Member member = (Member) memberService.loadUserByUsername(principal.getName());

        // 주문
        Long orderId = cartService.orderCartItem(orderItemRequests, member);

        Map<String, String> body = new HashMap<>();
        body.put("message", "주문 성공");
        return ResponseEntity.ok(body);
    }

    // 주문 내역 조회
    @GetMapping(value = {"/order/list"})
    public String orderHist(
            Principal principal,
            Model model,
            @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Member member = (Member) memberService.loadUserByUsername(principal.getName());

        // 날짜별 정렬 x
        Page<OrderHistoryResponse> orderHistoryPage = orderService.getOrderList(member, pageable);
        List<Integer> barNumbers = paginationService.getBarNumbers(pageable.getPageNumber(), orderHistoryPage.getTotalPages());
        model.addAttribute("orderHistoryPage", orderHistoryPage);
        model.addAttribute("barNumbers", barNumbers);

        return "order/orderHist";
    }

    @ApiOperation(value = "주문 상세 조회", notes = "주문 상세 조회")
    @PostMapping(value = "/order/api/{orderId}/cancel") @ResponseBody
    public ResponseEntity<?> orderCancel(
            @PathVariable(name = "orderId") Long orderId,
            Principal principal
    ) {
        Member member = (Member) memberService.loadUserByUsername(principal.getName());

        Order validatedOrder = orderService.validateOrder(orderId, member);
        orderService.cancel(validatedOrder);

        Map<String, String> body = new HashMap<>();
        body.put("message", "주문이 취소되었습니다.");

        return ResponseEntity.ok(body);
    }
}
