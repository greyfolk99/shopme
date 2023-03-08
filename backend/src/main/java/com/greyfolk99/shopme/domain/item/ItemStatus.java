package com.greyfolk99.shopme.domain.item;

import lombok.Getter;

public enum ItemStatus {
    ON_SALE ("판매중"),
    SOLD_OUT ("품절"),
    NOT_AVAILABLE ("판매중지")
    ;
    @Getter
    private String text;
    ItemStatus(String text) {
        this.text = text;
    }
}
