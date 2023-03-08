package com.greyfolk99.shopme.domain.order;

import com.greyfolk99.shopme.domain.BaseEntity;
import com.greyfolk99.shopme.domain.item.Item;
import lombok.*;

import javax.persistence.*;
@Table(name = "order_item")
@Getter @Entity
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
public class OrderItem extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "order_item_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "item_id")
    private Item item;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "order_id")
    private Order order;
    @Column
    private int orderPrice;
    @Column
    private int count;

    public static OrderItem of(Order order, Item item, int count) {
        return OrderItem.builder()
        .item(item)
        .count(count)
        .order(order)
        .orderPrice(item.getPrice())
        .build();
    }

    public int getTotalPrice() {
        return this.orderPrice * this.count;
    }

    public void cancel() {
        Item item = this.getItem();
        item.addStock(count);
        item.deductSoldCount(count);
    }
}
