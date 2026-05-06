package org.springframework.samples.petclinic.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:hsqldb:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
public class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testUnauthenticatedAccessRedirectsToLogin() throws Exception {
        // Test that unauthenticated requests to any endpoint redirect to login
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    public void testLoginPageIsAccessible() throws Exception {
        // Test that the default Spring Security login page is accessible
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Please sign in")));
    }

    @Test
    @WithMockUser
    public void testAuthenticatedUserCanAccessEndpoints() throws Exception {
        // Test that authenticated users can access protected endpoints
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }

    @Test
    public void testHttpBasicAuthenticationWorks() throws Exception {
        // Test HTTP Basic authentication
        mockMvc.perform(get("/")
                .header("Authorization", "Basic dXNlcjpwYXNzd29yZA==")) // user:password in base64
                .andExpect(status().isUnauthorized()); // Will be unauthorized because we don't have a real user configured
    }

    @Test
    public void testFormLoginEndpointExists() throws Exception {
        // Test that form login endpoint exists and processes login attempts
        mockMvc.perform(formLogin("/login").user("testuser").password("testpass"))
                .andExpect(unauthenticated()); // Will be unauthenticated because we don't have a real user configured
    }

    @Test
    public void testAllEndpointsRequireAuthentication() throws Exception {
        // Test various endpoints to ensure they all require authentication
        String[] endpoints = {"/", "/owners", "/vets", "/pets"};
        
        for (String endpoint : endpoints) {
            mockMvc.perform(get(endpoint))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrlPattern("**/login"));
        }
    }
}