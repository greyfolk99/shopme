package com.greyfolk99.shopme.dto.request;

import com.greyfolk99.shopme.domain.cart.CartItem;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "itemId")
public class CartItemRequest {
    @NotNull(message = "없는 상품입니다.")
    private Long itemId;
    @Min(value = 1, message = "최소 1개 이상 담아주세요")
    private int count;
    public CartItem toEntity() {
        return CartItem.ofAnonymous(this);
    }
}
