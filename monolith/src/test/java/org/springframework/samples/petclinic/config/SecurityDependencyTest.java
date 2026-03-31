package org.springframework.samples.petclinic.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class to verify that Spring Security dependencies are available on the classpath.
 * This ensures the spring-boot-starter-security dependency was properly added.
 */
public class SecurityDependencyTest {

    /**
     * Test that core Spring Security classes are available on the classpath.
     * This verifies that the spring-boot-starter-security dependency is properly configured.
     */
    @Test
    public void testSpringSecurityClassesAreAvailable() {
        // Test that key Spring Security classes can be loaded
        assertDoesNotThrow(() -> {
            Class.forName("org.springframework.security.config.annotation.web.configuration.EnableWebSecurity");
        }, "EnableWebSecurity class should be available");

        assertDoesNotThrow(() -> {
            Class.forName("org.springframework.security.web.SecurityFilterChain");
        }, "SecurityFilterChain class should be available");

        assertDoesNotThrow(() -> {
            Class.forName("org.springframework.security.core.userdetails.UserDetailsService");
        }, "UserDetailsService class should be available");

        assertDoesNotThrow(() -> {
            Class.forName("org.springframework.security.crypto.password.PasswordEncoder");
        }, "PasswordEncoder class should be available");

        assertDoesNotThrow(() -> {
            Class.forName("org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder");
        }, "BCryptPasswordEncoder class should be available");
    }

    /**
     * Test that Spring Security test classes are available (from spring-security-test dependency).
     */
    @Test
    public void testSpringSecurityTestClassesAreAvailable() {
        assertDoesNotThrow(() -> {
            Class.forName("org.springframework.security.test.context.support.WithMockUser");
        }, "WithMockUser class should be available for testing");

        assertDoesNotThrow(() -> {
            Class.forName("org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors");
        }, "SecurityMockMvcRequestPostProcessors class should be available for testing");
    }

    /**
     * Test that we can instantiate key Spring Security interfaces.
     */
    @Test
    public void testSpringSecurityInterfacesCanBeUsed() {
        // Verify that the interfaces exist and can be referenced
        assertNotNull(EnableWebSecurity.class, "EnableWebSecurity annotation should be available");
        assertNotNull(SecurityFilterChain.class, "SecurityFilterChain interface should be available");
        assertNotNull(UserDetailsService.class, "UserDetailsService interface should be available");
        assertNotNull(PasswordEncoder.class, "PasswordEncoder interface should be available");
    }
}