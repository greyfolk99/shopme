package com.greyfolk99.shopme.domain.item;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QItem is a Querydsl query type for Item
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QItem extends EntityPathBase<Item> {

    private static final long serialVersionUID = -974627126L;

    public static final QItem item = new QItem("item");

    public final com.greyfolk99.shopme.domain.QBaseEntity _super = new com.greyfolk99.shopme.domain.QBaseEntity(this);

    public final ListPath<com.greyfolk99.shopme.domain.cart.CartItem, com.greyfolk99.shopme.domain.cart.QCartItem> cartItems = this.<com.greyfolk99.shopme.domain.cart.CartItem, com.greyfolk99.shopme.domain.cart.QCartItem>createList("cartItems", com.greyfolk99.shopme.domain.cart.CartItem.class, com.greyfolk99.shopme.domain.cart.QCartItem.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isDeleted = createBoolean("isDeleted");

    public final StringPath itemDetail = createString("itemDetail");

    public final ListPath<ItemImage, QItemImage> itemImages = this.<ItemImage, QItemImage>createList("itemImages", ItemImage.class, QItemImage.class, PathInits.DIRECT2);

    public final StringPath itemName = createString("itemName");

    public final EnumPath<ItemStatus> itemStatus = createEnum("itemStatus", ItemStatus.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    //inherited
    public final StringPath modifiedBy = _super.modifiedBy;

    public final ListPath<com.greyfolk99.shopme.domain.order.OrderItem, com.greyfolk99.shopme.domain.order.QOrderItem> orderItems = this.<com.greyfolk99.shopme.domain.order.OrderItem, com.greyfolk99.shopme.domain.order.QOrderItem>createList("orderItems", com.greyfolk99.shopme.domain.order.OrderItem.class, com.greyfolk99.shopme.domain.order.QOrderItem.class, PathInits.DIRECT2);

    public final NumberPath<Integer> price = createNumber("price", Integer.class);

    public final NumberPath<Integer> soldCount = createNumber("soldCount", Integer.class);

    public final NumberPath<Integer> stock = createNumber("stock", Integer.class);

    public QItem(String variable) {
        super(Item.class, forVariable(variable));
    }

    public QItem(Path<? extends Item> path) {
        super(path.getType(), path.getMetadata());
    }

    public QItem(PathMetadata metadata) {
        super(Item.class, metadata);
    }

}

