package com.greyfolk99.shopme.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.greyfolk99.shopme.config.SecurityConfig;
import com.greyfolk99.shopme.dto.request.CartItemRequest;
import com.greyfolk99.shopme.service.CartService;
import com.greyfolk99.shopme.service.MemberService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseCookie;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.servlet.http.Cookie;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(CartController.class)
@Import(SecurityConfig.class)
public class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private MemberService memberService;
    @MockBean
    private CartService cartService;
    @Autowired
    ObjectMapper objectMapper;
    Gson gson = new Gson();

    private Cookie getCookie() throws Exception {
        Cookie cookie = new Cookie(
            "cartItems",
            URLEncoder.encode(gson.toJson(new ArrayList<>(Arrays.asList(
                new CartItemRequest(0L, 1),
                new CartItemRequest(1L, 1),
                new CartItemRequest(2L, 1)
            ))), StandardCharsets.UTF_8));

        MockHttpServletResponse response = new MockHttpServletResponse();
        response.addHeader("Set-Cookie", ResponseCookie
            .from(cookie.getName(), cookie.getValue())
            .maxAge(60 * 60 * 24 * 30)
            .path("/")
            .sameSite("None")
            .domain("")
            .secure(true)
                .build().toString());
        return response.getCookie(cookie.getName());
    }

    private List<CartItemRequest> decodeCookie(Cookie cookie) throws Exception {
        return gson.fromJson(
                URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8),
                new TypeToken<List<CartItemRequest>>() {}.getType());
    }

    @Test
    public void deleteCartItem_anonymous() throws Exception {
        // given
        Cookie cookie = getCookie();
        Long itemId = 1L;

        // when & then
        MockHttpServletResponse response = mockMvc.perform(
        delete("/cart/api/item")
            .contentType("application/json")
            .cookie(cookie)
            .param("itemId", itemId.toString())
            .with(csrf()).secure(true))
        .andExpect(status().isOk())
        .andReturn().getResponse();

        Cookie newCookie = response.getCookie("cartItems");
        assertEquals(decodeCookie(newCookie).size(), 2);
    }

    @Test
    public void addCartItem_anonymous() throws Exception {
        // given
        Cookie cookie = getCookie();
        CartItemRequest addingItem = new CartItemRequest(3L, 1);

        // when & then
        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders
        .post("/cart/api/item")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(addingItem))
            .with(csrf()).secure(true))
        .andExpect(status().isOk())
        .andExpect(cookie().exists("cartItems"))
        .andReturn().getResponse();

        Cookie newCookie = response.getCookie("cartItems");
        assertEquals(decodeCookie(newCookie).size(), 4);
    }

    @Test
    public void updateCartItem_anonymous() throws Exception {
        // given
        Cookie cookie = getCookie();
        Long updatingItemId = 0L;
        int updatingItemCount = 2;

        // when & then
        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders
        .patch("/cart/api/item")
            .cookie(cookie)
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(new CartItemRequest(updatingItemId, updatingItemCount)))
            .with(csrf()).secure(true))
        .andExpect(status().isOk())
        .andExpect(cookie().exists("cartItems"))
        .andReturn().getResponse();

        Cookie newCookie = response.getCookie("cartItems");
        assertEquals(decodeCookie(newCookie).get(0).getCount(), updatingItemCount);
    }
}
