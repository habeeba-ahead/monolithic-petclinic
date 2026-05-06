package org.springframework.samples.petclinic.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.context.annotation.Configuration;

import static org.junit.jupiter.api.Assertions.*;

public class SecurityConfigUnitTest {

    @Test
    public void testSecurityConfigClassExists() {
        // Test that SecurityConfig class exists
        assertDoesNotThrow(() -> {
            Class<?> securityConfigClass = Class.forName("org.springframework.samples.petclinic.config.SecurityConfig");
            assertNotNull(securityConfigClass);
        });
    }

    @Test
    public void testSecurityConfigHasEnableWebSecurityAnnotation() {
        // Test that SecurityConfig class has @EnableWebSecurity annotation
        assertDoesNotThrow(() -> {
            Class<?> securityConfigClass = Class.forName("org.springframework.samples.petclinic.config.SecurityConfig");
            assertTrue(securityConfigClass.isAnnotationPresent(EnableWebSecurity.class));
        });
    }

    @Test
    public void testSecurityConfigHasConfigurationAnnotation() {
        // Test that SecurityConfig class has @Configuration annotation
        assertDoesNotThrow(() -> {
            Class<?> securityConfigClass = Class.forName("org.springframework.samples.petclinic.config.SecurityConfig");
            assertTrue(securityConfigClass.isAnnotationPresent(Configuration.class));
        });
    }
}