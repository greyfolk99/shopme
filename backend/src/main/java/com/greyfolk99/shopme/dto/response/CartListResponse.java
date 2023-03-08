package com.greyfolk99.shopme.dto.response;

import com.greyfolk99.shopme.domain.cart.CartItem;
import com.greyfolk99.shopme.domain.item.Item;
import com.greyfolk99.shopme.domain.item.ItemImage;
import com.greyfolk99.shopme.domain.item.ItemImageType;
import com.greyfolk99.shopme.exception.ExceptionClass;
import com.greyfolk99.shopme.exception.rest.ResourceNotFoundException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.nio.file.Paths;

@Getter @Setter
@ToString
@EqualsAndHashCode(of = "itemId")
public class CartListResponse {

    // 비회원일때
    private Long itemId;
    private String itemName;
    private int price;
    private int count;
    private String imageSubPath;
    private String imageFilename;

    public String getFullImagePath(){
        return Paths.get(getImageSubPath(), getImageFilename()).toString();
    }

    public static CartListResponse of(
        Long itemId,
        String itemName,
        int price,
        int count,
        String imageSubPath,
        String imageFilename
    ){
        return new CartListResponse(itemId, itemName, price, count, imageSubPath, imageFilename);
    }

    public static CartListResponse of
    (
        CartItem cartItem
    ) {
        Item item = cartItem.getItem();
        ItemImage image = item.getItemImages().stream()
            .filter(i -> i.getItemImageType().equals(ItemImageType.THUMBNAIL))
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException(ExceptionClass.ITEM_IMG, "메인 이미지가 없습니다."));
        return new CartListResponse(
            item.getId(),
            item.getItemName(),
            item.getPrice(),
            cartItem.getCount(),
            image.getFileInfo().getSubPath(),
            image.getFileInfo().getFilename()
        );
    }

    public static CartListResponse of
    (
        Item item, int count
    ) {
        ItemImage image = item.getItemImages().stream()
            .filter(i -> i.getItemImageType() == ItemImageType.THUMBNAIL)
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException(ExceptionClass.ITEM_IMG, "썸네일이 없습니다."));
        return new CartListResponse(
            item.getId(),
            item.getItemName(),
            item.getPrice(),
            count,
            image.getFileInfo().getSubPath(),
            image.getFileInfo().getFilename()
        );
    }

    public CartListResponse
    (
        Long itemId,
        String itemName,
        int price,
        int count,
        String imageSubPath,
        String imageFilename
    ) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.price = price;
        this.count = count;
        this.imageSubPath = imageSubPath;
        this.imageFilename = imageFilename;
    }

}
