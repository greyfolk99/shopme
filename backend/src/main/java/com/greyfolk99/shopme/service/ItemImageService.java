package com.greyfolk99.shopme.service;

import com.greyfolk99.shopme.domain.resource.FileInfo;
import com.greyfolk99.shopme.domain.item.Item;
import com.greyfolk99.shopme.domain.resource.ImagePath;
import com.greyfolk99.shopme.domain.item.ItemImage;
import com.greyfolk99.shopme.domain.item.ItemImageType;
import com.greyfolk99.shopme.dto.form.ItemForm;
import com.greyfolk99.shopme.dto.request.ItemImageRequest;
import com.greyfolk99.shopme.exception.ExceptionClass;
import com.greyfolk99.shopme.exception.rest.ResourceNotFoundException;
import com.greyfolk99.shopme.repository.ItemImageRepository;
import com.greyfolk99.shopme.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ItemImageService {

    private final FileService fileService;
    private final ItemImageRepository itemImageRepository;
    private final ItemRepository itemRepository;

    @Value("${resource.image.path}")
    private String IMAGE_STORAGE_ROOT_PATH;

    // 상품 이미지 수정

    @Transactional(rollbackFor = Exception.class)
    public void updateItemImages(Item item, ItemForm itemForm) throws IOException {
        // 기존 이미지 삭제
        deleteImageFiles(item, itemForm);
        // 새로운 이미지는 저장
        saveItemImages(item, itemForm);
    }

    public void deleteItemImage(Long imageId) {
        ItemImage itemImage = itemImageRepository.findById(imageId).orElseThrow(()->
            new ResourceNotFoundException(ExceptionClass.ITEM_IMG, "해당 이미지를 찾을 수 없습니다."));
        itemImageRepository.delete(itemImage);
        try {
            fileService.deleteFile(Paths.get(
                IMAGE_STORAGE_ROOT_PATH,
                itemImage.getFileInfo().getFullPath()));
        } catch (FileNotFoundException e) {
            log.error("[deleteItemImage] imageId : {}, 이미지 파일이 존재하지 않습니다.", imageId);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteImageFiles(@NotNull Item item, ItemForm itemForm) throws FileNotFoundException {
        Set<Long> remainedIds = new HashSet<>(itemForm.getAllIds());
        Set<ItemImage> deletingItemImages = item.getItemImages().stream()
            .filter(itemImage -> !remainedIds.contains(itemImage.getId()))
            .collect(Collectors.toSet());
        Set<Long> deletingIds = deletingItemImages.stream()
            .map(ItemImage::getId)
            .collect(Collectors.toSet());
        itemImageRepository.deleteByIdIn(deletingIds);

        for (ItemImage deletingItemImage : deletingItemImages) {
            Path imagePath = Path.of(
                IMAGE_STORAGE_ROOT_PATH, // 이미지 저장소 디렉토리
                deletingItemImage.getFileInfo().getSubPath(), // 저장소 sub-path
                deletingItemImage.getFileInfo().getFilename()); // 파일명
            fileService.deleteFile(imagePath);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveItemImages(Item item, ItemForm itemForm) throws IOException {
        // 썸네일 저장
        List<MultipartFile> thumbnailFiles = itemForm.getThumbnailFiles();
        for (MultipartFile thumbnailFile : thumbnailFiles) {
            saveItemImage(item, ItemImageRequest.of(ItemImageType.THUMBNAIL, thumbnailFile));
        }
        // 상품 이미지 저장
        List<MultipartFile> productImageFiles = itemForm.getProductImageFiles();
        for (MultipartFile productImageFile : productImageFiles) {
            saveItemImage(item, ItemImageRequest.of(ItemImageType.PRODUCT, productImageFile));
        }
        // 상품 상세 이미지 저장
        List<MultipartFile> productDetailImageFiles = itemForm.getProductDetailImageFiles();
        for (MultipartFile productDetailImageFile : productDetailImageFiles) {
            saveItemImage(item, ItemImageRequest.of(ItemImageType.DETAIL, productDetailImageFile));
        }
    }

    // 상품 이미지 등록
    @Transactional(rollbackFor = Exception.class)
    public void saveItemImage(Item item, ItemImageRequest itemImageRequest) throws IOException {

        // 이미지 파일 업로드(UUID로 고유한 파일명으로 저장)
        final String originalImageName = itemImageRequest.getFile().getOriginalFilename();
        final String generatedImageName = fileService.generateUniqueFilename(originalImageName);
        Path path = Path.of(
            IMAGE_STORAGE_ROOT_PATH, // 이미지 저장소 디렉토리
            ImagePath.ITEM.getPathString(), // 상품 이미지 sub-path (image/item)
            generatedImageName // 파일명
        );
        fileService.uploadFile(path, itemImageRequest.getFile().getBytes());

        // 이미지 파일 정보 저장
        ItemImage itemImage = ItemImage.of(item, itemImageRequest.getItemImageType());
        itemImage.update(
            FileInfo.of(originalImageName, generatedImageName, ImagePath.ITEM.getPathString()), // 이미지 파일 정보
            itemImageRequest.getItemImageType()); // 상품 이미지 종류
        itemImageRepository.save(itemImage);
    }
}
