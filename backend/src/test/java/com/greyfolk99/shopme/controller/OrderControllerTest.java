package com.greyfolk99.shopme.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.greyfolk99.shopme.service.CartService;
import com.greyfolk99.shopme.service.OrderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

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

    @Test
    public void test_givenNotAuthenticated_whenRequestOrder_thenReturn401Status() throws Exception {
        // given
        Map<String, String> input = new HashMap<>();
        input.put("itemId", "19");
        input.put("count", "1");

        mockMvc.perform(post("/cart/item").content(objectMapper.writeValueAsString(input)))
            .andExpect(status().is(403)).andDo(print());
    }

    @Test
    public void testFor_givenNotAuthenticated_whenRequestOrder_thenRedirectToLoginPage() throws Exception{
        // given
        Map<String, String> input = new HashMap<>();
        input.put("itemId", "19");
        input.put("count", "1");

        mockMvc.perform(post("/cart/item").content(objectMapper.writeValueAsString(input)))
                .andExpect(status().is(403))
                .andExpect(redirectedUrl("/auth/login?error=true&exception=" + "로그인이 필요한 페이지입니다."));
    }
}
