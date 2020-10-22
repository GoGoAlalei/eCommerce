package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    private CartController cartController;

    private UserRepository userRepository = mock(UserRepository.class);

    private CartRepository cartRepository = mock(CartRepository.class);

    private ItemRepository itemRepository = mock(ItemRepository.class);

    private User user;
    private Item item;
    private Cart cart;
    private ModifyCartRequest request;

    @Before
    public void Setup() {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);

        user = new User();
        user.setId(1L);
        user.setUsername("test");
        user.setPassword("testPassword");

        item = new Item();
        item.setId(1L);
        item.setName("testItem");
        item.setPrice(BigDecimal.valueOf(3.33));
        item.setDescription("testItem");

        cart = new Cart();
        cart.setId(1L);
        cart.addItem(item);
        cart.setUser(user);

        user.setCart(cart);

        request = new ModifyCartRequest();
        request.setItemId(1L);
        request.setUsername("test");
        request.setQuantity(1);

        when(userRepository.findByUsername("test")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

    }

    @Test
    public void addToCart() {

        final ResponseEntity<Cart> responseEntity = cartController.addTocart(request);
        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());

        Cart c = responseEntity.getBody();
        assertNotNull(c);
        assertEquals(2, c.getItems().size());
        assertEquals(BigDecimal.valueOf(6.66), c.getTotal());
        assertEquals(item, c.getItems().get(1));
        assertEquals(user, c.getUser());
    }

    @Test
    public void AddUserNotFound() {
        request.setUsername("who");

        final ResponseEntity<Cart> responseEntity = cartController.addTocart(request);
        assertNotNull(responseEntity);
        assertNotEquals(200, responseEntity.getStatusCodeValue());
    }

    @Test
    public void AddItemNotFound() {
        request.setItemId(2L);

        final ResponseEntity<Cart> responseEntity = cartController.addTocart(request);
        assertNotNull(responseEntity);
        assertNotEquals(200, responseEntity.getStatusCodeValue());
    }

    @Test
    public void removeFromCart() {

        final ResponseEntity<Cart> responseEntity = cartController.removeFromcart(request);
        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());

        Cart c = responseEntity.getBody();
        assertNotNull(c);
        assertEquals(0, c.getItems().size());
        assertEquals(user, c.getUser());
    }

    @Test
    public void RemoveUserNotFound() {
        request.setUsername("who");

        final ResponseEntity<Cart> responseEntity = cartController.removeFromcart(request);
        assertNotNull(responseEntity);
        assertNotEquals(200, responseEntity.getStatusCodeValue());
    }

    @Test
    public void RemoveItemNotFound() {
        request.setItemId(2L);

        final ResponseEntity<Cart> responseEntity = cartController.removeFromcart(request);
        assertNotNull(responseEntity);
        assertNotEquals(200, responseEntity.getStatusCodeValue());
    }

}
