package com.greyfolk99.shopme.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.greyfolk99.shopme.config.SecurityConfig;
import com.greyfolk99.shopme.dto.form.ItemForm;
import com.greyfolk99.shopme.service.ItemService;
import com.greyfolk99.shopme.service.PaginationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(SpringRunner.class)
@WebMvcTest(AdminItemController.class)
@Import(SecurityConfig.class)
public class AdminItemControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemService itemService;
    @MockBean
    private PaginationService paginationService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void updateItem() throws Exception {
        mockMvc.perform(
            post("/admin/items/{id}", 1L)
            .content(objectMapper.writeValueAsString(
                new ItemForm())));
    }
}
