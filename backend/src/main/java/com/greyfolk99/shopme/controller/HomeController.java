package com.greyfolk99.shopme.controller;

import com.greyfolk99.shopme.dto.response.ItemPreviewResponse;
import com.greyfolk99.shopme.dto.form.SearchForm;
import com.greyfolk99.shopme.service.ItemService;
import com.greyfolk99.shopme.service.PaginationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ItemService itemService;
    private final PaginationService paginationService;

    @GetMapping(value = "/")
    public String home(
            HttpServletRequest request,
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "exception", required = false) String errorMessage,
            @ModelAttribute SearchForm searchForm,
            @PageableDefault(size = 12, sort = "soldCount", direction = Sort.Direction.DESC) Pageable homePageable,
            Model model
    ) {
        model.addAttribute("error", error);
        model.addAttribute("errorMessage", errorMessage);
        log.info("[home] request IP = {}", request.getRemoteAddr());
        model.addAttribute("searchForm", searchForm);
        Page<ItemPreviewResponse> homeItems = itemService.getHomeItemPage(searchForm, homePageable);
        List<Integer> barNumbers = paginationService.getBarNumbers(homePageable.getPageNumber(), homeItems.getTotalPages());
        model.addAttribute("homeItems", homeItems);
        model.addAttribute("barNumbers", barNumbers);

        return "home/home";
    }
}
