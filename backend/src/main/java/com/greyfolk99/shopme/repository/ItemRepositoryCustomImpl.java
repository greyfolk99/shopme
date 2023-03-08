package com.greyfolk99.shopme.repository;

import com.greyfolk99.shopme.domain.item.*;

import com.greyfolk99.shopme.domain.order.QOrderItem;
import com.greyfolk99.shopme.dto.form.SearchForm;
import com.greyfolk99.shopme.dto.response.ItemPreviewResponse;
import com.greyfolk99.shopme.dto.response.QItemPreviewResponse;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityManager;
import java.util.List;


public class ItemRepositoryCustomImpl implements ItemRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    public ItemRepositoryCustomImpl(EntityManager em) { this.queryFactory = new JPAQueryFactory(em); }
    QItem item = QItem.item;
    QItemImage itemImage = QItemImage.itemImage;
    QOrderItem orderItem = QOrderItem.orderItem;

    @Override
    public Page<Item> getItemPage(SearchForm searchForm, Pageable pageable) {
        List<Item> itemList = queryFactory
            .selectFrom(item)
            .where(
                createdAfter(searchForm.getItemSearchDateType()),
                searchSellStatusEq(searchForm.getItemStatus()),
                searchByLike(searchForm.getSearchBy(), searchForm.getSearchQuery()))
            .orderBy(item.id.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long totalCount = queryFactory
            .select(item.count())
            .from(item)
            .where(
                createdAfter(searchForm.getItemSearchDateType()),
                searchSellStatusEq(searchForm.getItemStatus()),
                searchByLike(searchForm.getSearchBy(), searchForm.getSearchQuery()))
            .fetchOne();

        return new PageImpl<>(itemList, pageable, totalCount == null ? 0 : totalCount);
    }
    private BooleanExpression createdAfter(ItemSearchDateType itemSearchDateType) {
        return itemSearchDateType == null || itemSearchDateType.equals(ItemSearchDateType.ALL) ?
                null : QItem.item.createdAt.after(itemSearchDateType.getStartTime()); }
    private BooleanExpression searchSellStatusEq(ItemStatus searchSellStatus){
        return searchSellStatus == null ? null : QItem.item.itemStatus.eq(searchSellStatus); }
    private BooleanExpression searchByLike(String searchBy, String searchQuery) {
        if (StringUtils.equals("itemName", searchBy)) {
            return item.itemName.like("%" + searchQuery + "%"); }
        if (StringUtils.equals("createdBy", searchBy)) {
            return item.createdBy.like("%" + searchQuery + "%"); }
        return null;
    }

    @Override
    public Page<ItemPreviewResponse> getHomeItemPage(SearchForm searchForm, Pageable pageable) {
        List<ItemPreviewResponse> homeItemPreviewResponseList = queryFactory
            .selectDistinct(new QItemPreviewResponse(
                item.id,
                item.itemName,
                item.itemDetail,
                itemImage.fileInfo.filename,
                itemImage.fileInfo.subPath,
                item.price
            ))
            .from(itemImage)
            .join(itemImage.item, item)
            .where(
                itemImage.itemImageType.eq(ItemImageType.THUMBNAIL),
                itemNameLikeOritemDetailLike(searchForm.getSearchQuery())
            )
            .orderBy(item.id.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
        Long totalCount = queryFactory
            .selectDistinct(item.count())
            .from(itemImage)
            .join(itemImage.item, item)
            .where(
                itemImage.itemImageType.eq(ItemImageType.THUMBNAIL),
                itemNameLikeOritemDetailLike(searchForm.getSearchQuery()))
            .fetchOne();
        System.out.println("totalCount = " + totalCount);
        return new PageImpl<>(homeItemPreviewResponseList, pageable, totalCount == null ? 0 : totalCount);
    }
    private BooleanExpression itemNameLikeOritemDetailLike(String searchQuery) {
        return StringUtils.isEmpty(searchQuery) ?
            null : item.itemName.like("%" + searchQuery + "%")
                .or(item.itemDetail.like("%" + searchQuery + "%"));
    }
}
