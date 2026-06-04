package org.springframework.samples.petclinic.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LoginControllerTest {

    @Test
    public void testLoginControllerCreation() {
        LoginController loginController = new LoginController();
        assertNotNull(loginController);
    }

    @Test
    public void testLoginMethodReturnsCorrectView() {
        LoginController loginController = new LoginController();
        String viewName = loginController.login();
        assertEquals("login", viewName);
    }
}