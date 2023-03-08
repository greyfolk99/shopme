package com.greyfolk99.shopme.domain.resource;

import com.greyfolk99.shopme.dto.response.ItemImageResponse;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FileInfo {
    @Column
    private String originalFilename; // 원본 이미지 파일명
    @Column
    private String filename; // 저장된 이미지 파일명
    @Column
    private String subPath; // 이미지 경로 (ex: "image/item")
    public void update(String originalName, String generatedName, String rootPath) {
        this.originalFilename = originalName;
        this.filename = generatedName;
        this.subPath = rootPath;
    }
    public static FileInfo of(ItemImageResponse dto) {
        return FileInfo.builder()
            .originalFilename(dto.getOriginalImageName())
            .filename(dto.getImageName())
            .subPath(dto.getSubPath())
                .build();
    }
    public static FileInfo of(String originalName, String generatedName, String rootPath) {
        return FileInfo.builder()
            .originalFilename(originalName)
            .filename(generatedName)
            .subPath(rootPath).build();
    }
    public String getFullPath() {
        return this.subPath + "/" + this.filename;
    }
}
