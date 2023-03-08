package com.greyfolk99.shopme.dto.request;

import com.greyfolk99.shopme.domain.item.ItemImageType;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class ItemImageRequest {
    private ItemImageType itemImageType;
    private MultipartFile file;
    public static ItemImageRequest of(ItemImageType itemImageType, MultipartFile file) {
        return new ItemImageRequest(itemImageType, file);
    }
}
