package com.greyfolk99.shopme.service;

import com.greyfolk99.shopme.domain.resource.FileInfo;
import com.greyfolk99.shopme.domain.item.Item;
import com.greyfolk99.shopme.domain.item.ItemStatus;
import com.greyfolk99.shopme.domain.item.ItemImage;
import com.greyfolk99.shopme.domain.item.ItemImageType;
import com.greyfolk99.shopme.repository.ItemImageRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
@DataJpaTest
public class ItemImageServiceTest {

    @InjectMocks
    ItemImageService itemImageService;
    @Mock
    ItemImageRepository itemImageRepository;
    @MockBean
    FileService fileService;

    private Item itemWithItemImages() {
        Item item = Item.of
        (
            0L,
            "상품명",
            "상품설명",
            10000,
            1,
            ItemStatus.ON_SALE
        );
        int i = 0;
        // thumbnail(0)
        for ( ; i <= 0; i++) {
            FileInfo thumbFileInfo = mock(FileInfo.class);
            ItemImage thumbItemImage = ItemImage.of((long) i, item, thumbFileInfo, ItemImageType.THUMBNAIL);
            item.getItemImages().add(thumbItemImage);
        }
        // product(1, 2)
        for ( ; i <= 2; i++) {
            FileInfo prodFileInfo = mock(FileInfo.class);
            ItemImage prodItemImage = ItemImage.of((long) i, item, prodFileInfo, ItemImageType.PRODUCT);
            item.getItemImages().add(prodItemImage);
        }
        // detail(3, 4)
        for ( ; i <= 4; i++) {
            FileInfo detailFileInfo = mock(FileInfo.class);
            ItemImage detailItemImage = ItemImage.of((long) i, item, detailFileInfo, ItemImageType.DETAIL);
            item.getItemImages().add(detailItemImage);
        }
        return item;
    }

    @Test
    public void delete_itemImage() throws Exception {
        // given
        Item item = itemWithItemImages();
        List<Long> remainedIdList = List.of(0L, 2L, 3L, 4L);

        // when
        Set<Long> remainedIds = new HashSet<>(remainedIdList);
        Set<ItemImage> deletingItemImages = item.getItemImages().stream()
            .filter(itemImage -> !remainedIds.contains(itemImage.getId()))
            .collect(Collectors.toSet());
        Set<Long> deletingIds = deletingItemImages.stream()
            .map(ItemImage::getId)
            .collect(Collectors.toSet());
        itemImageRepository.deleteByIdIn(deletingIds);

        // then
        assertEquals(deletingIds.size(), 1);
        System.out.println(deletingIds.toString());
        assertThrows(Exception.class, () -> itemImageRepository.findById(1L).orElseThrow(Exception::new));
    }
}
