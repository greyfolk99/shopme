package com.greyfolk99.shopme.dto.request;

import com.greyfolk99.shopme.domain.item.Item;
import lombok.*;

/*

 */
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"itemId", "count"})
public class OrderItemDetailRequest {
    private Long itemId;
    private int count;

    public OrderItemDetailRequest(OrderItemRequest orderItemRequest) {
        this.itemId = orderItemRequest.getItemId();
        this.count = orderItemRequest.getCount();
    }
    public static OrderItemDetailRequest of(OrderItemRequest orderItemRequest) {
        return new OrderItemDetailRequest(orderItemRequest);
    }
    public static OrderItemDetailRequest of(Item item, int count) {
        return new OrderItemDetailRequest(item.getId(), count);
    }
}
