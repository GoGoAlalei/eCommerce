package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;

    private UserRepository userRepository = mock(UserRepository.class);

    private CartRepository cartRepository = mock(CartRepository.class);

    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    private User user;

    @Before
    public void setUp() {
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);

        user = new User();
        user.setId(1L);
        user.setUsername("test");
        user.setPassword("testPassword");
    }

    @Test
    public void createUser() throws Exception {
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("test");
        r.setPassword("testPassword");
        r.setConfirmPassword("testPassword");

        when(encoder.encode("testPassword")).thenReturn("thisIsHashed");
        final ResponseEntity<User> responseEntity = userController.createUser(r);

        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());

        User u = responseEntity.getBody();
        assertNotNull(u);
        assertEquals(0, u.getId());
        assertEquals("test", u.getUsername());
        assertEquals("thisIsHashed", u.getPassword());

    }

    @Test
    public void invalidPassword() {
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("test");
        r.setPassword("test");
        r.setConfirmPassword("test");

        final ResponseEntity<User> responseEntity = userController.createUser(r);
        assertNotNull(responseEntity);
        assertNotEquals(200, responseEntity.getStatusCodeValue());
    }

    @Test
    public void findById() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        final ResponseEntity<User> responseEntity = userController.findById(1L);

        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());

        User u = responseEntity.getBody();
        assertNotNull(u);
        assertEquals(1L, u.getId());
        assertEquals("test", u.getUsername());
        assertEquals("testPassword", u.getPassword());
    }

    @Test
    public void findByUsername() {

        when(userRepository.findByUsername("test")).thenReturn(user);
        final ResponseEntity<User> responseEntity = userController.findByUserName("test");

        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());

        User u = responseEntity.getBody();
        assertNotNull(u);
        assertEquals(1L, u.getId());
        assertEquals("test", u.getUsername());
        assertEquals("testPassword", u.getPassword());
    }


}
