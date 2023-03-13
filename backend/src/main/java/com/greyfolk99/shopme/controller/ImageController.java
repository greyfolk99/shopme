package com.greyfolk99.shopme.controller;

import com.greyfolk99.shopme.domain.item.ItemImageType;
import com.greyfolk99.shopme.service.ItemImageService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ImageController {

    private final ItemImageService itemImageService;

    @ApiOperation(value = "상품 이미지 삭제", notes = "상품 이미지 삭제")
    @DeleteMapping(value = "/admin/item/image/api/{imageId}")
    public ResponseEntity<?> deleteItemImage(
            @PathVariable("imageId") Long imageId
    ) {
        itemImageService.deleteItemImage(imageId);
        Map<String, String> body = new HashMap<>();
        body.put("message", "상품 이미지 삭제 성공");
        return ResponseEntity.ok(body);
    }
}
