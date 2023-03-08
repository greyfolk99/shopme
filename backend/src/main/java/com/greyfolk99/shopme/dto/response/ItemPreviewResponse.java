package com.greyfolk99.shopme.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.nio.file.Paths;

@Getter @Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString
public class ItemPreviewResponse {

    private Long id;
    private String itemName;
    private String itemDetail;
    private String imageFilename;
    private String imageSubPath;
    private Integer price;

    @QueryProjection
    public ItemPreviewResponse
    (
        Long id,
        String itemName,
        String itemDetail,
        String imageFilename,
        String imageSubPath,
        Integer price
    ) {
        this.id = id;
        this.itemName = itemName;
        this.itemDetail = itemDetail;
        this.imageFilename = imageFilename;
        this.imageSubPath = imageSubPath;
        this.price = price;
    }

    public String getFullImagePath(){
        return Paths.get(imageSubPath).resolve(imageFilename).toString();
    }
}
