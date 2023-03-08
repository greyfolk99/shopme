package com.greyfolk99.shopme.repository;

import com.greyfolk99.shopme.domain.order.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
