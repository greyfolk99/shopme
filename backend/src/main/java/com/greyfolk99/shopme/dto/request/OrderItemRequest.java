package com.greyfolk99.shopme.dto.request;

import com.greyfolk99.shopme.domain.item.Item;
import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter @Setter
@Builder(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "itemId")
public class OrderItemRequest {
    @NotNull(message = "상품에 오류가 있습니다.")
    private Long itemId;
    @Min(value = 1, message = "최소 주문 수량은 1개 입니다.")
    @Max(value = 999, message = "최대 주문 수량은 999개 입니다.")
    private int count;
    public static OrderItemRequest from(Long itemId, int count) {
        return OrderItemRequest.builder()
            .itemId(itemId)
            .count(count)
            .build();
    }
    public static OrderItemRequest from(Item item, int count) {
        return OrderItemRequest.builder()
            .itemId(item.getId())
            .count(count)
            .build();
    }
}
