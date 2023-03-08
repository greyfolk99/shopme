package com.greyfolk99.shopme.repository;

import com.greyfolk99.shopme.domain.item.QItem;
import com.greyfolk99.shopme.domain.item.ItemImageType;
import com.greyfolk99.shopme.domain.item.QItemImage;
import com.greyfolk99.shopme.domain.order.Order;
import com.greyfolk99.shopme.domain.order.QOrderItem;
import com.greyfolk99.shopme.dto.response.OrderItemResponse;
import com.greyfolk99.shopme.dto.response.QOrderItemResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.util.List;


public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    public OrderRepositoryCustomImpl(EntityManager em) {this.queryFactory = new JPAQueryFactory(em);}
    QItem item = QItem.item;
    QOrderItem orderItem = QOrderItem.orderItem;
    QItemImage itemImage = QItemImage.itemImage;

    @Override
    public List<OrderItemResponse> getOrderItemResponses(Order order) {
        return queryFactory
            .select(new QOrderItemResponse(
                item.id,
                item.itemName,
                orderItem.count,
                orderItem.orderPrice,
                itemImage.fileInfo.subPath,
                itemImage.fileInfo.filename
            ))
            .from(orderItem)
            .join(item)
                .on(orderItem.item.eq(item))
            .join(itemImage)
                .on(item.id.eq(itemImage.item.id),
                    itemImage.itemImageType.eq(ItemImageType.THUMBNAIL))
            .where(orderItem.order.eq(order))
            .orderBy(orderItem.id.asc())
            .fetch();
    }
}
