package com.greyfolk99.shopme.repository;

import com.greyfolk99.shopme.domain.item.Item;
import com.greyfolk99.shopme.dto.response.ItemPreviewResponse;
import com.greyfolk99.shopme.dto.form.SearchForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ItemRepositoryCustom {

    Page<Item> getItemPage(SearchForm searchForm, Pageable pageable);
    // @QueryProjection 을 이용하여 바로 Dto 객체 반환
    Page<ItemPreviewResponse> getHomeItemPage(SearchForm searchForm, Pageable pageable);

}
