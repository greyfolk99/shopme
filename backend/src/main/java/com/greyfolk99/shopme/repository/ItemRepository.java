package com.greyfolk99.shopme.repository;

import com.greyfolk99.shopme.domain.cart.CartItem;
import com.greyfolk99.shopme.domain.item.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface ItemRepository extends JpaRepository<Item,Long>, ItemRepositoryCustom {
    Set<Item> findByIdIn(Collection<Long> itemIds);
    @Query("select i " +
            "from Item i " +
            "join fetch " +
            "   i.cartItems ci " +
            "   where ci in :cartItems")
    List<Item> findByCartItems(List<CartItem> cartItems);
}
