package com.greyfolk99.shopme.domain.item;

import lombok.Getter;

import java.time.LocalDateTime;

public enum ItemSearchDateType {
    ALL("전체기간", LocalDateTime.MIN),
    ONE_DAY("1일", LocalDateTime.now().minusDays(1)),
    ONE_WEEK("1주", LocalDateTime.now().minusWeeks(1)),
    ONE_MONTH("1개월", LocalDateTime.now().minusMonths(1)),
    SIX_MONTH("6개월", LocalDateTime.now().minusMonths(6));
    @Getter private final String dateType;
    private final LocalDateTime time;
    ItemSearchDateType(String dateType, LocalDateTime time) {
        this.time = time;
        this.dateType = dateType;
    }
    public LocalDateTime getStartTime(){
        return this.time;
    }
}
