package com.greyfolk99.shopme.dto.response;

import com.greyfolk99.shopme.domain.item.ItemImage;
import com.greyfolk99.shopme.domain.item.ItemImageType;
import lombok.*;

import java.nio.file.Paths;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString
@Builder(access = AccessLevel.PRIVATE)
public class ItemImageResponse {

    private Long id;
    private String subPath;
    private String imageName;
    private String originalImageName;
    private ItemImageType itemImageType;

    private String fullImagePath;

    public static ItemImageResponse toResponse(ItemImage itemImage) {
        String fullImagePath =
            Paths.get(itemImage.getFileInfo().getSubPath())
                .resolve(
            Paths.get(itemImage.getFileInfo().getFilename()))
                .toString();
        return ItemImageResponse.builder()
            .id(itemImage.getId())
            .imageName(itemImage.getFileInfo().getFilename())
            .subPath(itemImage.getFileInfo().getSubPath())
            .originalImageName(itemImage.getFileInfo().getOriginalFilename())
            .itemImageType(itemImage.getItemImageType())
            .fullImagePath(fullImagePath)
            .build();
    }
}
