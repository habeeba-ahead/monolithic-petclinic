package org.springframework.samples.petclinic.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:hsqldb:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
public class ComprehensiveSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testSpringSecurityDependencyExists() {
        // Verify spring-boot-starter-security dependency is available
        assertDoesNotThrow(() -> {
            Class.forName("org.springframework.security.config.annotation.web.configuration.EnableWebSecurity");
            Class.forName("org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter");
            Class.forName("org.springframework.security.config.annotation.web.builders.HttpSecurity");
        });
    }

    @Test
    public void testSecurityConfigClassExistsWithAnnotations() {
        // Verify SecurityConfig class exists with proper annotations
        assertDoesNotThrow(() -> {
            Class<?> securityConfigClass = Class.forName("org.springframework.samples.petclinic.config.SecurityConfig");
            assertTrue(securityConfigClass.isAnnotationPresent(EnableWebSecurity.class));
            assertTrue(securityConfigClass.isAnnotationPresent(org.springframework.context.annotation.Configuration.class));
        });
    }

    @Test
    public void testBasicHttpAuthenticationEnabled() throws Exception {
        // Test that HTTP Basic authentication is enabled for all endpoints
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    public void testDefaultLoginPageAccessible() throws Exception {
        // Test that the default Spring Security login page is accessible
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Please sign in")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Username")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Password")));
    }

    @Test
    @WithMockUser
    public void testAuthenticatedAccessWorks() throws Exception {
        // Test that authenticated users can access protected endpoints
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }

    @Test
    public void testAllEndpointsRequireAuthentication() throws Exception {
        // Test that various endpoints require authentication
        String[] protectedEndpoints = {"/", "/owners", "/vets"};
        
        for (String endpoint : protectedEndpoints) {
            mockMvc.perform(get(endpoint))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrlPattern("**/login"));
        }
    }
}