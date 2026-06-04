package org.springframework.samples.petclinic.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

public class SecurityConfigUnitTest {

    @Test
    public void testSecurityConfigCreation() {
        SecurityConfig securityConfig = new SecurityConfig();
        assertNotNull(securityConfig);
    }

    @Test
    public void testPasswordEncoderCreation() {
        SecurityConfig securityConfig = new SecurityConfig();
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();
        assertNotNull(passwordEncoder);
        
        String encoded = passwordEncoder.encode("test");
        assertTrue(passwordEncoder.matches("test", encoded));
        assertFalse(passwordEncoder.matches("wrong", encoded));
    }

    @Test
    public void testUserDetailsServiceCreation() {
        SecurityConfig securityConfig = new SecurityConfig();
        UserDetailsService userDetailsService = securityConfig.userDetailsService();
        assertNotNull(userDetailsService);
        
        // Test admin user
        UserDetails admin = userDetailsService.loadUserByUsername("admin");
        assertNotNull(admin);
        assertEquals("admin", admin.getUsername());
        assertTrue(admin.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
        
        // Test vet user
        UserDetails vet = userDetailsService.loadUserByUsername("vet");
        assertNotNull(vet);
        assertEquals("vet", vet.getUsername());
        assertTrue(vet.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_VET")));
        
        // Test owner user
        UserDetails owner = userDetailsService.loadUserByUsername("owner");
        assertNotNull(owner);
        assertEquals("owner", owner.getUsername());
        assertTrue(owner.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_OWNER")));
    }

    @Test
    public void testInvalidUserThrowsException() {
        SecurityConfig securityConfig = new SecurityConfig();
        UserDetailsService userDetailsService = securityConfig.userDetailsService();
        
        assertThrows(Exception.class, () -> {
            userDetailsService.loadUserByUsername("nonexistent");
        });
    }
}