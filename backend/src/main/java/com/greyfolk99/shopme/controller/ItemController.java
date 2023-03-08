package com.greyfolk99.shopme.controller;

import com.greyfolk99.shopme.dto.response.ItemDetailResponse;
import com.greyfolk99.shopme.service.ItemImageService;
import com.greyfolk99.shopme.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;
    private final ItemImageService itemImageService;
    // 상품 상세 페이지
    @GetMapping(value = "/item/{itemId}")
    public String itemDetail(
            @PathVariable("itemId") Long itemId,
            Model model
    ) {
        ItemDetailResponse itemDetailResponse = itemService.getItemDetail(itemId);
        model.addAttribute("itemId", itemId);
        model.addAttribute("item", itemDetailResponse);
        return "item/itemDetail";
    }
}
