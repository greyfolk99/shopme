package com.greyfolk99.shopme.dto.request;

import lombok.Data;

import javax.validation.constraints.Size;
import java.util.List;

@Data
public class OrderRequest {
    @Size(min = 1, message = "주문할 상품이 없습니다. ")
    private List<OrderItemRequest> orderItemRequests;
}
