package com.greyfolk99.shopme.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.greyfolk99.shopme.domain.item.Item;
import com.greyfolk99.shopme.domain.item.ItemStatus;
import com.greyfolk99.shopme.dto.request.OrderItemRequest;
import com.greyfolk99.shopme.service.CartService;
import com.greyfolk99.shopme.service.MemberService;
import com.greyfolk99.shopme.service.OrderService;
import com.greyfolk99.shopme.service.PaginationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private OrderService orderService;
    @MockBean
    private CartService cartService;
    @MockBean
    private MemberService memberService;
    @MockBean
    private PaginationService paginationService;

    @Test
    @WithMockUser(roles = "MEMBER", username = "admin@shopme.space")
    public void givenAuthenticated_whenPostOrder_thenReturn200Status() throws Exception{
        // given
        OrderItemRequest orderItemRequest = new OrderItemRequest(244L, 1);

        // when
        mockMvc.perform(post("/order/api/item")
            .with(csrf())
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(orderItemRequest)))
        // then
        .andExpect(status().is(200));
    }

    @Test
    public void givenNotAuthenticated_whenPostOrder_thenReturn403Status() throws Exception{
        // given
        OrderItemRequest orderItemRequest = new OrderItemRequest(244L, 1);

        // when
        mockMvc.perform(post("/order/api/item")
            .with(csrf())
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(orderItemRequest)))

        // then
        .andExpect(redirectedUrl("http://localhost/auth/login"));
    }
}
