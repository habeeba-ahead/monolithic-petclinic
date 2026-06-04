package org.springframework.samples.petclinic.security;

import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.config.SecurityConfig;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class SecurityConfigTest {

    @Test
    void testSecurityConfigCreation() {
        SecurityConfig config = new SecurityConfig();
        assertNotNull(config);
    }

    @Test
    void testPasswordEncoderBean() {
        SecurityConfig config = new SecurityConfig();
        PasswordEncoder encoder = config.passwordEncoder();
        assertNotNull(encoder);
        
        // Test that password encoding works
        String encoded = encoder.encode("password");
        assertTrue(encoder.matches("password", encoded));
        assertFalse(encoder.matches("wrongpassword", encoded));
    }

    @Test
    void testUserDetailsServiceBean() {
        SecurityConfig config = new SecurityConfig();
        UserDetailsService userDetailsService = config.userDetailsService();
        assertNotNull(userDetailsService);
        
        // Test that admin user exists and has ADMIN role
        UserDetails adminUser = userDetailsService.loadUserByUsername("admin");
        assertNotNull(adminUser);
        assertEquals("admin", adminUser.getUsername());
        assertTrue(adminUser.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
        
        // Test that regular user exists and has USER role
        UserDetails regularUser = userDetailsService.loadUserByUsername("user");
        assertNotNull(regularUser);
        assertEquals("user", regularUser.getUsername());
        assertTrue(regularUser.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }
}