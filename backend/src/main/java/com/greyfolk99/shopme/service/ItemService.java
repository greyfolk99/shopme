package com.greyfolk99.shopme.service;

import com.greyfolk99.shopme.domain.item.Item;
import com.greyfolk99.shopme.domain.item.ItemImage;
import com.greyfolk99.shopme.dto.form.ItemForm;
import com.greyfolk99.shopme.dto.form.SearchForm;
import com.greyfolk99.shopme.dto.response.ItemDetailResponse;
import com.greyfolk99.shopme.dto.response.ItemPreviewResponse;
import com.greyfolk99.shopme.exception.ExceptionClass;
import com.greyfolk99.shopme.exception.rest.ResourceNotFoundException;
import com.greyfolk99.shopme.repository.ItemImageRepository;
import com.greyfolk99.shopme.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemImageService itemImageService;

    // 중복 메소드
    public Item findItemById(Long id){
        return itemRepository.findById(id).orElseThrow(()->new ResourceNotFoundException(
                ExceptionClass.ITEM, HttpStatus.NOT_FOUND, "상품이 존재하지 않습니다."));}

    // 상품 저장
    @Transactional(rollbackFor = Exception.class)
    public Long saveItem(ItemForm itemForm) throws IOException {
        // 상품 저장
        Item item = itemRepository.save(Item.from(itemForm));
        // 상품 이미지 저장
        itemImageService.saveItemImages(item, itemForm);
        return item.getId();
    }

    // 상품 수정
    @Transactional(rollbackFor = Exception.class)
    public void updateItem(Long itemId, ItemForm itemForm) throws IOException {
        // 상품 영속화
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ResourceNotFoundException(
            ExceptionClass.ITEM, HttpStatus.BAD_REQUEST, "잘못된 요청입니다." ));
        // 영속화된 Item 수정
        item.update(itemForm);
        // 상품 이미지 수정
        itemImageService.updateItemImages(item, itemForm);
    }

    // 상품 관리 페이지 상품 목록 조회
    @Transactional(readOnly = true)
    public Page<Item> getAdminItemPage(SearchForm searchForm, Pageable pageable) {
        return itemRepository.getItemPage(searchForm, pageable);
    }

    // 수정용 Form 가져오기
    @Transactional(readOnly = true)
    public ItemForm getItemFormForUpdate(Long itemId) {
        // 상품 영속
        Item item = findItemById(itemId);
        List<ItemImage> itemImages = item.getItemImages();

        return ItemForm.forUpdate(item, itemImages);
    }

    // 상세 페이지 가져오기
    @Transactional(readOnly = true)
    public ItemDetailResponse getItemDetail(Long itemId) {
        // 상품 영속
        Item item = findItemById(itemId);
        List<ItemImage> itemImages = item.getItemImages();
        return ItemDetailResponse.of(item, itemImages);
    }

    // 메인 페이지 상품 목록 조회
    @Transactional(readOnly = true)
    public Page<ItemPreviewResponse> getHomeItemPage(SearchForm searchForm, Pageable pageable){
        return itemRepository.getHomeItemPage(searchForm, pageable);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteItem(Long itemId) {
        itemRepository.deleteById(itemId);
    }
}
