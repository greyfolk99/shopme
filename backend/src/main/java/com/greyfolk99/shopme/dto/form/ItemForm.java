package com.greyfolk99.shopme.dto.form;

import com.greyfolk99.shopme.domain.item.Item;
import com.greyfolk99.shopme.domain.item.ItemStatus;
import com.greyfolk99.shopme.domain.item.ItemImage;
import com.greyfolk99.shopme.domain.item.ItemImageType;
import com.greyfolk99.shopme.dto.response.ItemImageResponse;
import lombok.*;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemForm {

    @NotBlank(message = "상품명은 필수 입력 사항입니다.")
    private String itemName;
    @NotNull(message = "가격은 필수 입력 사항입니다.")
    private Integer price;
    @NotBlank(message = "상품 상세는 필수 입력 사항입니다.")
    private String itemDetail;
    @NotNull(message = "재고는 필수 입력 값입니다.")
    private Integer stock;
    @NotNull(message = "상품 상태는 필수 입력 사항입니다.")
    private ItemStatus itemStatus;

    // post
    private List<Long> thumbnailIds = new ArrayList<>();
    private List<Long> productImageIds = new ArrayList<>();
    private List<Long> productDetailImageIds = new ArrayList<>();
    private List<MultipartFile> thumbnailFiles = new ArrayList<>();
    private List<MultipartFile> productImageFiles = new ArrayList<>();
    private List<MultipartFile> productDetailImageFiles = new ArrayList<>();

    // get
    private List<ItemImageResponse> thumbnailResponses = new ArrayList<>();
    private List<ItemImageResponse> productImageResponses = new ArrayList<>();
    private List<ItemImageResponse> productDetailImageResponses = new ArrayList<>();

    @AssertTrue(message = "썸네일은 1개만 업로드 가능합니다.")
    public boolean isThumbnailValid() {
        return ItemImageType.THUMBNAIL.getAvailCount().contains(thumbnailIds.size() + thumbnailFiles.size());
    }
    @AssertTrue(message = "상품 이미지는 1개 이상 10개 이하로 업로드 해야 합니다.")
    public boolean isProductImageValid() {
        return ItemImageType.PRODUCT.getAvailCount().contains(productImageFiles.size() + productImageIds.size());
    }
    @AssertTrue(message = "상품 상세 이미지는 10개 이하로 업로드 해야 합니다.")
    public boolean isProductDetailImageValid() {
        return ItemImageType.DETAIL.getAvailCount().contains(productDetailImageFiles.size() + productDetailImageIds.size());
    }

    public List<Long> getAllIds() {
        List<Long> allIds = new ArrayList<>();
        allIds.addAll(thumbnailIds);
        allIds.addAll(productImageIds);
        allIds.addAll(productDetailImageIds);
        return allIds;
    }
    public static ItemForm forUpdate(Item item, List<ItemImage> itemImages) {
        Map<ItemImageType, List<ItemImageResponse>> imageMap = itemImages.stream()
            .map(ItemImageResponse::toResponse)
            .collect(Collectors.groupingBy(ItemImageResponse::getItemImageType));
        return new ItemForm(
            item.getItemName(),
            item.getPrice(),
            item.getItemDetail(),
            item.getStock(),
            item.getItemStatus(),
            imageMap.get(ItemImageType.THUMBNAIL),
            imageMap.get(ItemImageType.PRODUCT),
            imageMap.get(ItemImageType.DETAIL));
    }

    private ItemForm
    (
        String itemName,
        int price,
        String itemDetail,
        int stock,
        ItemStatus itemStatus,
        List<ItemImageResponse> thumbnailResponses,
        List<ItemImageResponse> productImageResponses,
        List<ItemImageResponse> productDetailImageResponses
    ) {
        this.itemName = itemName;
        this.price = price;
        this.itemDetail = itemDetail;
        this.stock = stock;
        this.itemStatus = itemStatus;
        this.thumbnailResponses = thumbnailResponses;
        this.productImageResponses = productImageResponses;
        this.productDetailImageResponses = productDetailImageResponses;
    }
}
