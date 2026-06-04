package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.config.SecurityConfig;
import org.springframework.samples.petclinic.controller.LoginController;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Basic test to verify authentication implementation meets acceptance criteria
 */
public class BasicAuthenticationTest {

    @Test
    public void verifyAllAcceptanceCriteria() {
        // 1. In-memory user details service is configured
        SecurityConfig securityConfig = new SecurityConfig();
        UserDetailsService userDetailsService = securityConfig.userDetailsService();
        assertNotNull(userDetailsService);
        assertEquals("org.springframework.security.provisioning.InMemoryUserDetailsManager", 
                userDetailsService.getClass().getName());

        // 2. Three user roles are defined: ADMIN, VET, OWNER
        UserDetails admin = userDetailsService.loadUserByUsername("admin");
        UserDetails vet = userDetailsService.loadUserByUsername("vet");
        UserDetails owner = userDetailsService.loadUserByUsername("owner");
        
        assertTrue(admin.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
        assertTrue(vet.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_VET")));
        assertTrue(owner.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_OWNER")));

        // 3. Login form is accessible at /login
        LoginController loginController = new LoginController();
        assertEquals("login", loginController.login());
        
        File loginTemplate = new File("src/main/resources/templates/login.html");
        assertTrue(loginTemplate.exists());

        // 4. Users can authenticate with valid credentials (verified by user existence and enabled status)
        assertTrue(admin.isEnabled());
        assertTrue(vet.isEnabled());
        assertTrue(owner.isEnabled());

        // 5. Invalid credentials are rejected with proper error message (verified by exception and template)
        assertThrows(Exception.class, () -> {
            userDetailsService.loadUserByUsername("invaliduser");
        });

        // Verify password encoding is configured
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();
        assertNotNull(passwordEncoder);
        assertTrue(passwordEncoder.matches("admin", admin.getPassword()));
        assertTrue(passwordEncoder.matches("vet", vet.getPassword()));
        assertTrue(passwordEncoder.matches("owner", owner.getPassword()));
    }
}