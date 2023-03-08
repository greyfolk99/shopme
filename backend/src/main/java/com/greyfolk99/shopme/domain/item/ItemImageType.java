package com.greyfolk99.shopme.domain.item;

import lombok.Getter;

import java.util.List;
import java.util.stream.IntStream;

public enum ItemImageType {
    THUMBNAIL(List.of(1)),
    PRODUCT(IntStream.rangeClosed(1, 10).boxed().toList()),
    DETAIL(IntStream.rangeClosed(0, 10).boxed().toList())
    ;
    @Getter private List<Integer> availCount;
    ItemImageType(List<Integer> availCount) {
        this.availCount = availCount;
    }
}
