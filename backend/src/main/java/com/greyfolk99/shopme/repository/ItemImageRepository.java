package com.greyfolk99.shopme.repository;

import com.greyfolk99.shopme.domain.item.ItemImage;
import com.greyfolk99.shopme.domain.item.ItemImageType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ItemImageRepository extends JpaRepository<ItemImage, Long> {
    void deleteByIdIn(Collection<Long> deletingIds);
}
