package com.greyfolk99.shopme.dto.response;

import com.greyfolk99.shopme.domain.order.Order;
import com.greyfolk99.shopme.domain.order.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class OrderHistoryResponse {

    private Long orderId;
    private String orderDate;
    private OrderStatus orderStatus;
    private int totalPrice;
    private List<OrderItemResponse> orderItemResponseList = new ArrayList<>();

    public static OrderHistoryResponse from(Order order) {
        return new OrderHistoryResponse(order);
    }
    public void addOrderItem(OrderItemResponse orderItemResponse) {
        orderItemResponseList.add(orderItemResponse);
    }
    public void addOrderItems(List<OrderItemResponse> orderItemResponseList) {
        this.orderItemResponseList.addAll(orderItemResponseList);
    }

    private OrderHistoryResponse(Order order) {
        this.orderId = order.getId();
        this.orderDate = order.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        this.orderStatus = order.getOrderStatus();
        this.totalPrice = order.getTotalPrice();
    }
}
