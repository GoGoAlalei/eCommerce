package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    private ItemController itemController;

    private ItemRepository itemRepository = mock(ItemRepository.class);

    private Item item;
    private List<Item> items;

    @Before
     public void Setup() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);

        item = new Item();
        item.setId(1L);
        item.setName("test");
        item.setPrice(BigDecimal.valueOf(3.99));
        item.setDescription("test");

        items = new ArrayList<>();
        items.add(item);



    }

    @Test
    public void getItems() {

        when(itemRepository.findAll()).thenReturn(items);
        final ResponseEntity<List<Item>> responseEntity = itemController.getItems();
        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());

        List<Item> testitems = responseEntity.getBody();
        assertNotNull(testitems);
        assertArrayEquals(items.toArray(), testitems.toArray());
    }

    @Test
    public void getItemById() {

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        final ResponseEntity<Item> responseEntity = itemController.getItemById(1L);
        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());

        Item testitem = responseEntity.getBody();
        assertNotNull(testitem);
        assertEquals("test", testitem.getName());
        assertEquals((BigDecimal.valueOf(3.99)),testitem.getPrice());
        assertEquals("test", testitem.getDescription());

    }

    @Test
    public void getItemsByName() {

        when(itemRepository.findByName("test")).thenReturn(items);
        final ResponseEntity<List<Item>> responseEntity = itemController.getItemsByName("test");
        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());

        List<Item> nameitems = responseEntity.getBody();
        assertNotNull(nameitems);
        assertArrayEquals(items.toArray(), nameitems.toArray());
    }
}
