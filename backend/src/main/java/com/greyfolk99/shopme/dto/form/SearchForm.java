package com.greyfolk99.shopme.dto.form;

import com.greyfolk99.shopme.domain.item.ItemSearchDateType;
import com.greyfolk99.shopme.domain.item.ItemStatus;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SearchForm {
    private ItemSearchDateType itemSearchDateType;
    private ItemStatus itemStatus;
    private String searchBy;
    private String searchQuery;
}

