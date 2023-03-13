package com.greyfolk99.shopme.controller;


import com.google.gson.reflect.TypeToken;
import com.greyfolk99.shopme.controller.handler.CookieHandler;
import com.greyfolk99.shopme.domain.cart.Cart;
import com.greyfolk99.shopme.domain.member.Member;
import com.greyfolk99.shopme.dto.request.CartItemRequest;
import com.greyfolk99.shopme.dto.response.CartListResponse;
import com.greyfolk99.shopme.exception.ExceptionClass;
import com.greyfolk99.shopme.exception.rest.ResourceNotFoundException;
import com.greyfolk99.shopme.exception.rest.ValidationFailedException;
import com.greyfolk99.shopme.service.CartService;
import com.greyfolk99.shopme.service.MemberService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.*;

@Slf4j
@Controller
@RequiredArgsConstructor
public class CartController {

    private final MemberService memberService;
    private final CartService cartService;
    private final CookieHandler cookieHandler;

    @ApiOperation(value = "장바구니에 상품 추가", notes = "장바구니에 상품을 추가합니다. (비회원은 쿠키에 저장)")
    @PostMapping(value = "/cart/api/item") @ResponseBody
    public ResponseEntity<?> addCart(
            HttpServletRequest request, Principal principal,
            @CookieValue(name = "cartItems", required = false) Cookie cookie,
            @RequestBody @Valid CartItemRequest cartItemRequest, BindingResult bindingResult,
            HttpServletResponse response
            ) {

        Map<String, String> body = new HashMap<>();

        // Validation
        if (bindingResult.hasErrors()) {
            StringBuilder sb = new StringBuilder();
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                sb.append(fieldError.getDefaultMessage());
            }
            throw new ValidationFailedException(ExceptionClass.CART_ITEM, sb.toString());
        }
        // 비회원 (이미 쿠키에 있는 상품 ? 수량만 더해줌 : 새로 리스트에 추가하고 쿠키 업데이트)
        if (principal == null || principal instanceof AnonymousAuthenticationToken) {
            List<CartItemRequest> cartItemsFromCookie = cookie == null ?
                new ArrayList<>() : cookieHandler.decode(cookie, new TypeToken<List<CartItemRequest>>(){});

            cartItemsFromCookie.stream()
                .filter(cartItemFromCookie -> cartItemFromCookie.getItemId().equals(cartItemRequest.getItemId()))
                .findAny()
                .ifPresentOrElse(
                    existingCartItem -> existingCartItem.setCount(existingCartItem.getCount() + cartItemRequest.getCount()),
                    () -> cartItemsFromCookie.add(cartItemRequest)
                );

            ResponseCookie responseCookie = cookieHandler.encode(
                "cartItems", cartItemsFromCookie, 60 * 60 * 24 * 7);
            response.setHeader("Set-Cookie", responseCookie.toString());
            response.setHeader("Cache-Control", "no-cache");
            body.put("message", "장바구니에 상품이 추가되었습니다.");
            return ResponseEntity.ok(body);
        }
        // 회원 (DB에 추가)
        else {
            System.out.println("회원");
            Member member = (Member) memberService.loadUserByUsername(principal.getName());
            Long cartItemId = cartService.addCartItem(cartItemRequest, member);
            body.put("message", "장바구니에 상품이 추가되었습니다.");
            return ResponseEntity.ok().body(body); // text
        }
    }

    @GetMapping("/cart")
    public String cartList(
            HttpServletResponse response, Principal principal,
            Model model,
            @CookieValue(name="cartItems", required=false) Cookie cookie
    ) {

        try {
            // 쿠키 파싱
            List<CartItemRequest> cookieCartItems = cookie == null ?
                new ArrayList<>() : cookieHandler.decode(cookie, new TypeToken<List<CartItemRequest>>() {
            });
            // 비회원 (쿠키에서 상품 불러옴)
            List<CartListResponse> cartListResponseList;
            if (principal == null || principal instanceof AnonymousAuthenticationToken) {
                cartListResponseList = cartService.getCartList(cookieCartItems);
            }
            // 회원 (쿠키 + DB 합친 후 DB에 저장, 쿠키 삭제)
            else {
                Member member = (Member) memberService.loadUserByUsername(principal.getName());
                Cart cart = cartService.mergeCart(cookieCartItems, member);
                cartListResponseList = cartService.getCartList(cart);
                ResponseCookie responseCookie = cookieHandler.encode("cartItems", new ArrayList<>(), 0);
                response.setHeader("Set-Cookie", responseCookie.toString());
                response.setHeader("Cache-Control", "no-cache");
            }
            model.addAttribute("cartItems", cartListResponseList);
            return "cart/cartList";

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return "redirect:/?error=true&exception=" + URLEncoder.encode(ex.getMessage(), StandardCharsets.UTF_8);
        }
    }

    @ApiOperation(value = "장바구니 상품 수량 갱신", notes = "장바구니 상품의 수량을 갱신합니다. (비회원은 쿠키에서 갱신)")
    @PatchMapping(value = "/cart/api/item") @ResponseBody
    public ResponseEntity<?> updateCartItem(
            HttpServletResponse response, Principal principal,
            @CookieValue(name = "cartItems", required = false) Cookie cookie,
            @RequestBody @Valid CartItemRequest cartItemRequest, BindingResult bindingResult
    ) {
        Map<String, String> body = new HashMap<>();

        // Validation
        if (bindingResult.hasErrors()) {
            StringBuilder sb = new StringBuilder();
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                sb.append(fieldError.getDefaultMessage());
            }
            throw new ValidationFailedException(ExceptionClass.CART_ITEM, sb.toString());
        }
        // 비회원 (쿠키에서 일치하는 상품 찾아서 수량 갱신)
        if (principal == null || principal instanceof AnonymousAuthenticationToken) {
            List<CartItemRequest> cartItemsFromCookie = cookie == null ?
                    new ArrayList<>() : cookieHandler.decode(cookie, new TypeToken<List<CartItemRequest>>(){});
            cartItemsFromCookie.stream()
                    .filter(existingCartItem -> existingCartItem.getItemId().equals(cartItemRequest.getItemId()))
                    .findAny()
                    .ifPresentOrElse(
                            existingCartItem -> existingCartItem.setCount(cartItemRequest.getCount()),
                            () -> {throw new ResourceNotFoundException(ExceptionClass.CART_ITEM, "해당 상품이 장바구니에 없습니다.");});
            ResponseCookie responseCookie = cookieHandler.encode(
                    "cartItems", cartItemsFromCookie, 60 * 60 * 24 * 7);
            response.setHeader("Set-Cookie", responseCookie.toString());
            response.setHeader("Cache-Control", "no-cache");
            body.put("message", "장바구니 상품 수량이 갱신되었습니다.");
            return ResponseEntity.ok(body);
        }
        // 회원 (DB에서 일치하는 상품 찾아서 수량 갱신)
        else {
            Member member = (Member) memberService.loadUserByUsername(principal.getName());
            Long cartItemId = cartService.updateCartItemCount(cartItemRequest, member);
            body.put("message", "장바구니 상품 수량이 갱신되었습니다.");
            return ResponseEntity.ok(body);
        }
    }

    @ApiOperation(value = "장바구니 상품 삭제", notes = "장바구니 상품을 삭제합니다. (비회원은 쿠키에서 삭제)")
    @DeleteMapping(value = "/cart/api/item") @ResponseBody
    public ResponseEntity<?> deleteCartItem(
        HttpServletRequest request, HttpServletResponse response, Principal principal,
        @CookieValue(name = "cartItems", required = false) Cookie cookie,
        @RequestParam("itemId") Long itemId
    ) {
        Map<String, String> body = new HashMap<>();

        // 비회원 (쿠키에서 아이디 일치하는 상품 제거 후 업데이트)
        List<CartItemRequest> before = cookie == null ?
            new ArrayList<>() : cookieHandler.decode(cookie, new TypeToken<List<CartItemRequest>>(){});
        List<CartItemRequest> after = before
            .stream().filter(cartItem -> !Objects.equals(cartItem.getItemId(), itemId))
            .toList();
        ResponseCookie responseCookie = cookieHandler.encode(
            "cartItems", after, 60 * 60 * 24 * 7);
        response.setHeader("Set-Cookie", responseCookie.toString());
        response.setHeader("Cache-Control", "no-cache");

        // 회원 (DB에서 삭제)
        if(principal != null && (before.size() == after.size())) {
            Member member = (Member) memberService.loadUserByUsername(principal.getName());
            cartService.deleteCartItem(itemId, member);
        }

        body.put("message", "장바구니 상품이 삭제되었습니다.");
        return ResponseEntity.ok(body);
    }
}

