package com.greyfolk99.shopme.domain.order;


import com.greyfolk99.shopme.domain.BaseEntity;
import com.greyfolk99.shopme.domain.member.Member;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(name = "orders")
@Entity @Getter @Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
public class Order extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "order_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "member_id")
    private Member member;
    private LocalDateTime orderDate; // 주문일
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus; // 주문상태

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();

    public void addOrderItem(OrderItem orderItem){
        this.orderItems.add(orderItem);
    }
    public void addOrderItems(List<OrderItem> orderItems){
        this.orderItems.addAll(orderItems);
    }
    public static Order of(Member member) {
        return Order.builder()
            .member(member)
            .orderStatus(OrderStatus.PENDING)
            .build();
    }
    public int getTotalPrice() {
        return orderItems.stream().mapToInt(OrderItem::getTotalPrice).sum();
    }
    public void cancel() {
        this.orderStatus = OrderStatus.CANCELLED;
        this.orderItems.forEach(OrderItem::cancel);
    }
    public void confirm() {
        this.orderStatus = OrderStatus.CONFIRMED;
        this.orderDate = LocalDateTime.now();
    }
}

