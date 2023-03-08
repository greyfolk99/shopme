package com.greyfolk99.shopme.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.nio.file.Paths;

@Getter @Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "itemId")
@ToString
public class OrderItemResponse {

    private Long itemId;
    private String itemName;
    private int count;
    private int orderPrice;
    private String imageSubPath;
    private String imageFilename;

    private String fullImagePath;

    @QueryProjection
    public OrderItemResponse
    (
        Long itemId,
        String itemName,
        int count,
        int orderPrice,
        String imageSubPath,
        String imageFilename
    ) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.count = count;
        this.orderPrice = orderPrice;
        this.imageSubPath = imageSubPath;
        this.imageFilename = imageFilename;
        this.fullImagePath = Paths.get(imageSubPath).resolve(imageFilename).toString();
    }
}
