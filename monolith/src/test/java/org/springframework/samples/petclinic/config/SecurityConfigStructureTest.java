package org.springframework.samples.petclinic.config;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class to verify the structure and annotations of SecurityConfig class.
 * This ensures the acceptance criteria regarding class annotations are met.
 */
public class SecurityConfigStructureTest {

    /**
     * Test that SecurityConfig class exists and has the required annotations.
     */
    @Test
    public void testSecurityConfigClassExists() {
        // Verify the class exists
        assertDoesNotThrow(() -> {
            Class.forName("org.springframework.samples.petclinic.config.SecurityConfig");
        }, "SecurityConfig class should exist");
    }

    /**
     * Test that SecurityConfig class has @Configuration annotation.
     */
    @Test
    public void testSecurityConfigHasConfigurationAnnotation() {
        Class<?> securityConfigClass = SecurityConfig.class;
        
        assertTrue(securityConfigClass.isAnnotationPresent(Configuration.class),
                "SecurityConfig should have @Configuration annotation");
    }

    /**
     * Test that SecurityConfig class has @EnableWebSecurity annotation.
     */
    @Test
    public void testSecurityConfigHasEnableWebSecurityAnnotation() {
        Class<?> securityConfigClass = SecurityConfig.class;
        
        assertTrue(securityConfigClass.isAnnotationPresent(EnableWebSecurity.class),
                "SecurityConfig should have @EnableWebSecurity annotation");
    }

    /**
     * Test that SecurityConfig extends WebSecurityConfigurerAdapter (for Spring Boot 2.x).
     */
    @Test
    public void testSecurityConfigExtendsWebSecurityConfigurerAdapter() {
        Class<?> securityConfigClass = SecurityConfig.class;
        
        assertTrue(WebSecurityConfigurerAdapter.class.isAssignableFrom(securityConfigClass),
                "SecurityConfig should extend WebSecurityConfigurerAdapter");
    }

    /**
     * Test that SecurityConfig has the required methods for security configuration.
     */
    @Test
    public void testSecurityConfigHasRequiredMethods() {
        Class<?> securityConfigClass = SecurityConfig.class;
        
        // Check for configure method
        assertDoesNotThrow(() -> {
            Method configureMethod = securityConfigClass.getDeclaredMethod("configure", 
                org.springframework.security.config.annotation.web.builders.HttpSecurity.class);
            assertNotNull(configureMethod, "configure method should exist");
        }, "SecurityConfig should have configure method");

        // Check for userDetailsService method
        assertDoesNotThrow(() -> {
            Method userDetailsServiceMethod = securityConfigClass.getDeclaredMethod("userDetailsService");
            assertNotNull(userDetailsServiceMethod, "userDetailsService method should exist");
        }, "SecurityConfig should have userDetailsService method");

        // Check for passwordEncoder method
        assertDoesNotThrow(() -> {
            Method passwordEncoderMethod = securityConfigClass.getDeclaredMethod("passwordEncoder");
            assertNotNull(passwordEncoderMethod, "passwordEncoder method should exist");
        }, "SecurityConfig should have passwordEncoder method");
    }
}