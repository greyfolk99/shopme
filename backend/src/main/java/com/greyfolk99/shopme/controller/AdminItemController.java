package com.greyfolk99.shopme.controller;


import com.greyfolk99.shopme.domain.item.Item;
import com.greyfolk99.shopme.dto.form.ItemForm;
import com.greyfolk99.shopme.dto.form.SearchForm;
import com.greyfolk99.shopme.service.ItemService;
import com.greyfolk99.shopme.service.PaginationService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminItemController {
    private final ItemService itemService;
    private final PaginationService paginationService;

    // 상품 등록 페이지 GET
    @GetMapping(value = "/item/new")
    public String itemForm(
            Principal principal,
            @ModelAttribute("itemForm") ItemForm itemForm,
            Model model
    ) {
        return "admin/item/itemForm";
    }

    // 상품 등록 POST 요청
    @PostMapping(value = "/item/new")
    public String itemNew(
            Principal principal,
            @Valid @ModelAttribute ItemForm itemForm,
            BindingResult bindingResult,
            Model model
    ) {
        // 유효성 검증 실패
        if (bindingResult.hasErrors()) {
            model.addAttribute("itemForm", itemForm);
            return "admin/item/itemForm";
        }
        // 상품 등록
        try {
            Long itemId = itemService.saveItem(itemForm);
        }
        // 예외 상황 발생 시 메세지 출력
        catch (Exception e) {
            model.addAttribute("itemForm", itemForm);
            model.addAttribute("exception", "상품 등록 중 에러가 발생하였습니다.");
            System.out.println(e.getMessage());
            return "admin/item/itemForm";
        }
        return "redirect:/";
    }

    // 상품 수정 페이지 GET
    @GetMapping(value = "/item/{itemId}")
    public String itemUpdatePage(
            Principal principal,
            @ModelAttribute("itemForm") ItemForm itemForm,
            @PathVariable Long itemId,
            Model model
            ) {
        try {
            itemForm = itemService.getItemFormForUpdate(itemId);
            model.addAttribute("itemId", itemId);
            model.addAttribute("itemForm", itemForm);
        }
        catch (Exception e) {
            model.addAttribute("exception", "상품을 불러오는 중 오류가 있습니다.");
            return "redirect:/";
        }
        return "admin/item/itemForm";
    }

    // 상품 수정
    @PostMapping(value = "/item/{itemId}")
    public String itemUpdate(
        @Valid @ModelAttribute ItemForm itemForm,
        @PathVariable Long itemId,
        BindingResult bindingResult,
        Model model
        ) {
        // 유효성 검증 실패 시
        if (bindingResult.hasErrors()) {
            model.addAttribute("itemId", itemId);
            model.addAttribute("itemForm", itemForm);
            return "admin/item/itemForm";
        }
        // 상품 수정
        try {
            itemService.updateItem(itemId, itemForm);
        }
        catch (Exception e) {
            model.addAttribute("itemId", itemId);
            model.addAttribute("itemForm", itemForm);
            model.addAttribute("exception", "상품 수정 중 오류가 발생했습니다.");
            return "admin/item/itemForm";
        }
        return "redirect:/";
    }

    @ApiOperation(value = "상품 삭제", notes = "상품 삭제")
    @DeleteMapping("/item/{itemId}")
    public ResponseEntity<?> deleteItem(
            @PathVariable Long itemId
    ) {
        itemService.deleteItem(itemId);
        return ResponseEntity.ok(itemId);
    }

    // 상품 관리 페이지
    @GetMapping(value = {"/items", "/items/{page}"})
    public String itemManagePage(
            SearchForm searchForm,
            @PathVariable(name = "page") Optional<Integer> page,
            Model model
    ) {
        // PageRequest.of() 메소드를 통해 Pageable 객체 생성
        Pageable pageable = PageRequest.of(page.orElse(1) - 1, 10);

        Page<Item> items = itemService.getAdminItemPage(searchForm, pageable);
        List<Integer> barNumbers = paginationService.getBarNumbers(pageable.getPageNumber(), items.getTotalPages());
        model.addAttribute("items", items);
        model.addAttribute("searchForm", searchForm);
        model.addAttribute("barNumbers", barNumbers);

        return "admin/item/itemManager";
    }
}
