package com.greyfolk99.shopme.domain.item;

import com.greyfolk99.shopme.domain.cart.CartItem;
import com.greyfolk99.shopme.domain.BaseEntity;
import com.greyfolk99.shopme.domain.order.OrderItem;
import com.greyfolk99.shopme.dto.form.ItemForm;
import com.greyfolk99.shopme.exception.ExceptionClass;
import com.greyfolk99.shopme.exception.rest.OutOfStockException;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.http.HttpStatus;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(
    name = "item",
    indexes = {
        @Index(columnList = "itemName"),
    })
@Getter @Entity
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Where(clause = "is_deleted = 0")
@SQLDelete(sql = "UPDATE item SET is_deleted = 1, deleted_at = NOW() WHERE item_id = ?")
@EqualsAndHashCode(of = "id")
public class Item extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "item_id")
    private Long id;
    @Column(nullable = false)
    private String itemName;
    @Lob @Column(nullable = false)
    private String itemDetail;
    @Column(nullable = false)
    private int price;
    @Column(nullable = false)
    private int stock;
    @Enumerated(EnumType.STRING)
    private ItemStatus itemStatus;
    @Column(columnDefinition = "boolean default 0") @Builder.Default
    private Boolean isDeleted = false;
    private LocalDateTime deletedAt;
    @Column
    private int soldCount;

    public static Item ofProxy(Long id) {
        return Item.builder().id(id).build();
    }

    @OneToMany(mappedBy = "item", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}) @ToString.Exclude @Builder.Default
    private List<ItemImage> itemImages = new ArrayList<>();
    @OneToMany(mappedBy = "item", cascade = CascadeType.REMOVE) @ToString.Exclude @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();
    @OneToMany(mappedBy = "item", cascade = CascadeType.REMOVE) @ToString.Exclude @Builder.Default
    private List<CartItem> cartItems = new ArrayList<>();

    public static Item from
    (
        ItemForm itemForm
    ) {
        return Item.builder()
            .itemName(itemForm.getItemName())
            .itemDetail(itemForm.getItemDetail())
            .price(itemForm.getPrice())
            .stock(itemForm.getStock())
            .itemStatus(itemForm.getItemStatus())
            .soldCount(0)
            .build();
    }
    public static Item of
    (
        String itemName,
        String itemDetail,
        int price,
        int stock,
        ItemStatus itemStatus
    ) {
        return Item.builder()

            .itemName(itemName)
            .itemDetail(itemDetail)
            .price(price)
            .stock(stock)
            .itemStatus(itemStatus)
            .soldCount(0)
            .build();
    }
    public static Item of
    (
        Long id,
        String itemName,
        String itemDetail,
        int price,
        int stock,
        ItemStatus itemStatus
    ) {
        return Item.builder()

            .id(id)
            .itemName(itemName)
            .itemDetail(itemDetail)
            .price(price)
            .stock(stock)
            .itemStatus(itemStatus)
            .soldCount(0)
            .build();
    }

    public void update
    (
        ItemForm itemForm
    ) {
        this.itemName = itemForm.getItemName();
        this.price = itemForm.getPrice();
        this.stock = itemForm.getStock();
        this.itemDetail = itemForm.getItemDetail();
        this.itemStatus = itemForm.getItemStatus();
    }

    /**
        재고 뺄셈
        @param stock 뺄 재고
        @throws OutOfStockException 재고가 부족한 경우
    **/
    public void removeStock(int stock) throws OutOfStockException {
        int restStock = this.stock - stock;
        if (restStock < 0) throw new OutOfStockException("상품의 재고가 부족합니다. (현재 재고 수량: " + this.stock + ")");
        this.stock = restStock;
        updateSellStatus();
    }

    public void sold(int soldCount) {
        this.soldCount += soldCount;
        removeStock(soldCount);
    }

    public void deductSoldCount(int soldCount) {
        this.soldCount -= soldCount;
    }

    public void addStock(int stock) {
        this.stock += stock;
        updateSellStatus();
    }

    public void updateSellStatus() {
        if (this.stock <= 0) {
            this.itemStatus = ItemStatus.SOLD_OUT;
        }
        if (this.stock > 0 && this.itemStatus.equals(ItemStatus.SOLD_OUT))  {
            this.itemStatus = ItemStatus.ON_SALE;
        }
    }
}
