package com.greyfolk99.shopme.domain.item;

import com.greyfolk99.shopme.domain.BaseEntity;
import com.greyfolk99.shopme.domain.resource.FileInfo;
import com.greyfolk99.shopme.dto.response.ItemImageResponse;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity @Getter @Table(name = "item_image")
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@EqualsAndHashCode(of = "id", callSuper = false)
@Where(clause = "is_deleted = 0")
@SQLDelete(sql = "UPDATE item_image SET is_deleted = 1 WHERE item_image_id = ?")
public class ItemImage extends BaseEntity {

    @Id @Column(name="item_image_id") @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JoinColumn(name = "item_id") @ManyToOne(fetch = FetchType.LAZY) @ToString.Exclude
    private Item item;
    @Embedded
    private FileInfo fileInfo;
    @Enumerated(EnumType.STRING)
    private ItemImageType itemImageType;
    @Column(columnDefinition = "boolean default 0") @Builder.Default
    private Boolean isDeleted = false;
    private LocalDateTime deletedAt;

    public static ItemImage of
    (
        Item item,
        ItemImageType itemImageType
    ) {
        return ItemImage.builder()
            .item(item)
            .itemImageType(itemImageType)
            .build();
    }

    public static ItemImage of
    (
        Long id,
        Item item,
        FileInfo fileInfo,
        ItemImageType itemImageType
    ) {
        return ItemImage.builder()
            .id(id)
            .item(item)
            .fileInfo(fileInfo)
            .itemImageType(itemImageType)
            .build();
    }

    public void update(FileInfo fileInfo, ItemImageType itemImageType) {
        this.fileInfo = fileInfo;
        this.itemImageType = itemImageType;
    }

    public ItemImageResponse toResponse() {
        return ItemImageResponse.toResponse(this);
    }
}