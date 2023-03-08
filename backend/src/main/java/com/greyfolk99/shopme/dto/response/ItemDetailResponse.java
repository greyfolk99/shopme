package com.greyfolk99.shopme.dto.response;

import com.greyfolk99.shopme.domain.item.Item;
import com.greyfolk99.shopme.domain.item.ItemStatus;
import com.greyfolk99.shopme.domain.item.ItemImage;
import com.greyfolk99.shopme.domain.item.ItemImageType;
import lombok.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter @Setter @Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemDetailResponse {

    private String itemName;
    private Integer price;
    private String itemDetail;
    private Integer stock;
    private ItemStatus itemStatus;

    // get
    private ItemImageResponse thumbnailResponse;
    private List<ItemImageResponse> productImageResponses;
    private List<ItemImageResponse> productDetailImageResponses;

    public static ItemDetailResponse of(Item item, List<ItemImage> itemImages) {
        Map<ItemImageType, List<ItemImageResponse>> imageMap = itemImages.stream()
            .map(ItemImageResponse::toResponse)
            .collect(Collectors.groupingBy(ItemImageResponse::getItemImageType));

        return ItemDetailResponse.builder()
            .itemName(item.getItemName())
            .price(item.getPrice())
            .itemDetail(item.getItemDetail())
            .stock(item.getStock())
            .itemStatus(item.getItemStatus())
            .thumbnailResponse(imageMap.get(ItemImageType.THUMBNAIL).get(0))
            .productImageResponses(imageMap.get(ItemImageType.PRODUCT))
            .productDetailImageResponses(imageMap.get(ItemImageType.DETAIL))
            .build();
    }

    public static ItemDetailResponse empty() {
        return new ItemDetailResponse();
    }
}
