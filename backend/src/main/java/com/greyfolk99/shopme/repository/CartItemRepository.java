package com.greyfolk99.shopme.repository;

import com.greyfolk99.shopme.domain.cart.CartItem;
import com.greyfolk99.shopme.dto.response.CartListResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByCartIdAndItemId(Long cartId, Long itemId);

    @Query("select new com.greyfolk99.shopme.dto.response.CartListResponse(" +
            "i.id, i.itemName, i.price, ci.count, im.fileInfo.subPath, im.fileInfo.filename) " +
            "from CartItem ci, ItemImage im " +
            "join ci.item i " +
            "where ci.cart.id = :cartId " +
            "and im.item.id = ci.item.id " +
            "and im.itemImageType = 'THUMBNAIL'" +
            "order by ci.createdAt desc" )
    List<CartListResponse> findCartListResponse(Long cartId);

    @Query("select ci " +
            "from CartItem ci " +
            "join fetch ci.item i " +
            "join fetch ci.cart c " +
            "where c.id = :cartId")
    List<CartItem> findCartItemsJoinFetchItemAndCartByCartId(Long cartId);
}

