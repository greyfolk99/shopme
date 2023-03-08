package com.greyfolk99.shopme.dto.response;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * com.greyfolk99.shopme.dto.response.QItemPreviewResponse is a Querydsl Projection type for ItemPreviewResponse
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QItemPreviewResponse extends ConstructorExpression<ItemPreviewResponse> {

    private static final long serialVersionUID = -196823234L;

    public QItemPreviewResponse(com.querydsl.core.types.Expression<Long> id, com.querydsl.core.types.Expression<String> itemName, com.querydsl.core.types.Expression<String> itemDetail, com.querydsl.core.types.Expression<String> imageFilename, com.querydsl.core.types.Expression<String> imageSubPath, com.querydsl.core.types.Expression<Integer> price) {
        super(ItemPreviewResponse.class, new Class<?>[]{long.class, String.class, String.class, String.class, String.class, int.class}, id, itemName, itemDetail, imageFilename, imageSubPath, price);
    }

}

