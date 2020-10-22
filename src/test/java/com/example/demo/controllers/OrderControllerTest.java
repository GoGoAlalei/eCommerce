package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private OrderController orderController;

    private UserRepository userRepository = mock(UserRepository.class);

    private OrderRepository orderRepository = mock(OrderRepository.class);

    private User user;
    private Item item;
    private Cart cart;

    @Before
    public void Setup() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);

        user = new User();
        user.setId(1L);
        user.setUsername("test");
        user.setPassword("testPassword");

        item = new Item();
        item.setId(1L);
        item.setName("testItem");
        item.setPrice(BigDecimal.valueOf(3.99));
        item.setDescription("testItem");

        cart = new Cart();
        cart.setId(1L);
        cart.addItem(item);
        cart.setUser(user);

        user.setCart(cart);

        when(userRepository.findByUsername("test")).thenReturn(user);

    }

    @Test
    public void submitOrder() {

        final ResponseEntity<UserOrder> responseEntity = orderController.submit("test");
        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());

        UserOrder testorder = responseEntity.getBody();
        assertNotNull(testorder);
        assertEquals(1,testorder.getItems().size());
        assertEquals("testItem", testorder.getItems().get(0).getName());
        assertEquals(1L,testorder.getUser().getId());
        assertEquals(BigDecimal.valueOf(3.99),testorder.getTotal());
    }

    @Test
    public void submitUserNotFound() {
        final ResponseEntity<UserOrder> responseEntity = orderController.submit("who");
        assertNotNull(responseEntity);
        assertNotEquals(200, responseEntity.getStatusCodeValue());
    }

    @Test
    public void getOrderhistoryForUser() {

        UserOrder order = new UserOrder();
        order.setId(1L);
        List<Item> items = new ArrayList<>();
        items.add(item);
        order.setItems(items);
        order.setUser(user);
        order.setTotal(BigDecimal.valueOf(3.99));

        List<UserOrder> orders = new ArrayList<>();
        orders.add(order);

        when(orderRepository.findByUser(user)).thenReturn(orders);
        final ResponseEntity<List<UserOrder>> responseEntity = orderController.getOrdersForUser("test");
        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());

        List<UserOrder> testhistory = responseEntity.getBody();
        assertNotNull(testhistory);
        assertEquals(1,testhistory.size());
        assertEquals("testItem", testhistory.get(0).getItems().get(0).getName());
        assertEquals(BigDecimal.valueOf(3.99),testhistory.get(0).getTotal());
    }

    @Test
    public void historyUserNotFound() {
        UserOrder order = new UserOrder();
        order.setId(1L);
        List<Item> items = new ArrayList<>();
        items.add(item);
        order.setItems(items);
        order.setUser(user);
        order.setTotal(BigDecimal.valueOf(3.99));

        List<UserOrder> orders = new ArrayList<>();
        orders.add(order);

        when(orderRepository.findByUser(user)).thenReturn(orders);
        final ResponseEntity<List<UserOrder>> responseEntity = orderController.getOrdersForUser("who");
        assertNotNull(responseEntity);
        assertNotEquals(200, responseEntity.getStatusCodeValue());
    }
}
