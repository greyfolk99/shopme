package com.greyfolk99.shopme.dto.response;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * com.greyfolk99.shopme.dto.response.QOrderItemResponse is a Querydsl Projection type for OrderItemResponse
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QOrderItemResponse extends ConstructorExpression<OrderItemResponse> {

    private static final long serialVersionUID = 797794186L;

    public QOrderItemResponse(com.querydsl.core.types.Expression<Long> itemId, com.querydsl.core.types.Expression<String> itemName, com.querydsl.core.types.Expression<Integer> count, com.querydsl.core.types.Expression<Integer> orderPrice, com.querydsl.core.types.Expression<String> imageSubPath, com.querydsl.core.types.Expression<String> imageFilename) {
        super(OrderItemResponse.class, new Class<?>[]{long.class, String.class, int.class, int.class, String.class, String.class}, itemId, itemName, count, orderPrice, imageSubPath, imageFilename);
    }

}

