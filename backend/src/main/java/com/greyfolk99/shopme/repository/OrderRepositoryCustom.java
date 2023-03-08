package com.greyfolk99.shopme.repository;

import com.greyfolk99.shopme.domain.order.Order;
import com.greyfolk99.shopme.dto.response.OrderItemResponse;

import java.util.List;

public interface OrderRepositoryCustom {
    List<OrderItemResponse> getOrderItemResponses(Order order);
}
