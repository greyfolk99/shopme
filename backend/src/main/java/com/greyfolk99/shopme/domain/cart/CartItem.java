package com.greyfolk99.shopme.domain.cart;

import com.greyfolk99.shopme.domain.BaseEntity;
import com.greyfolk99.shopme.domain.item.Item;
import com.greyfolk99.shopme.dto.request.CartItemRequest;
import com.greyfolk99.shopme.dto.response.CartListResponse;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.PositiveOrZero;


@Getter @Entity @Table(name = "cart_item")
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartItem extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "cart_item_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "cart_id")
    private Cart cart;
    @ManyToOne @JoinColumn(name = "item_id")
    private Item item;
    @PositiveOrZero
    private int count;

    public static CartItem of(Cart cart, Item item, int count) {
        return CartItem.builder().cart(cart).item(item).count(count).build();
    }
    public static CartItem ofAnonymous(Item item, int count) {
        return CartItem.builder().item(item).count(count).build();
    }
    public static CartItem ofAnonymous(CartItemRequest request) {
        return CartItem.builder().item(Item.ofProxy(request.getItemId())).count(request.getCount()).build();
    }

    public CartItem addCount(int count) {
        this.count += count;
        return this;
    }
    public void updateCount(int count) {
        this.count = count;
    }

    public CartListResponse toResponse() {
        return CartListResponse.of(this);
    }
}
