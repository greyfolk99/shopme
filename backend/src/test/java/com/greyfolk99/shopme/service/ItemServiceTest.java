package com.greyfolk99.shopme.service;

import com.greyfolk99.shopme.domain.item.Item;
import com.greyfolk99.shopme.domain.item.ItemStatus;
import com.greyfolk99.shopme.repository.ItemImageRepository;
import com.greyfolk99.shopme.repository.ItemRepository;
import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.event.annotation.BeforeTestExecution;

@RunWith(MockitoJUnitRunner.class)
@DataJpaTest
public class ItemServiceTest {
    @InjectMocks
    ItemService itemService;
    @MockBean
    ItemRepository itemRepository;

    @Before
    public void setUpItems() {
        Item item = Item.of(0L, "상품명", "상품설명", 10000, 1, ItemStatus.ON_SALE);
        itemRepository.save(item);
    }
}
