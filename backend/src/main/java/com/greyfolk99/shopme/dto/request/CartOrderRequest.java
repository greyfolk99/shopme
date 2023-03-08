package com.greyfolk99.shopme.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class CartOrderRequest {
    @Size(min = 1, message = "주문할 상품이 없습니다. ")
    private List<Long> itemIds = new ArrayList<>();
}
