package com.greyfolk99.shopme.repository;

import com.greyfolk99.shopme.domain.cart.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart,Long> {
    Optional<Cart> findByMemberUuid(UUID memberId);
}
