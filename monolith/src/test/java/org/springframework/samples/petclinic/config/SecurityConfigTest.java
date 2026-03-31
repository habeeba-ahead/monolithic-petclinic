package org.springframework.samples.petclinic.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@SpringJUnitConfig
@AutoConfigureWebMvc
public class SecurityConfigTest {

    @Autowired
    private SecurityConfig securityConfig;

    @Autowired
    private SecurityFilterChain securityFilterChain;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void testSecurityConfigExists() {
        assertNotNull(securityConfig, "SecurityConfig bean should be created");
    }

    @Test
    public void testSecurityConfigHasEnableWebSecurityAnnotation() {
        assertTrue(SecurityConfig.class.isAnnotationPresent(EnableWebSecurity.class),
                "SecurityConfig should have @EnableWebSecurity annotation");
    }

    @Test
    public void testSecurityFilterChainBeanExists() {
        assertNotNull(securityFilterChain, "SecurityFilterChain bean should be created");
    }

    @Test
    public void testUserDetailsServiceBeanExists() {
        assertNotNull(userDetailsService, "UserDetailsService bean should be created");
    }

    @Test
    public void testPasswordEncoderBeanExists() {
        assertNotNull(passwordEncoder, "PasswordEncoder bean should be created");
    }

    @Test
    public void testDefaultUserExists() {
        assertNotNull(userDetailsService.loadUserByUsername("user"),
                "Default user should exist in UserDetailsService");
    }
}