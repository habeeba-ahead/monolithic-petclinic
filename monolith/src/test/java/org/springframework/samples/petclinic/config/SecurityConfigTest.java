package org.springframework.samples.petclinic.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;

/**
 * Test class to verify Spring Security configuration meets acceptance criteria:
 * 1. spring-boot-starter-security dependency is available
 * 2. SecurityConfig class exists with @EnableWebSecurity annotation
 * 3. Basic HTTP authentication is enabled for all endpoints
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:hsqldb:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
public class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Test that unauthenticated requests to any endpoint are rejected with 401 Unauthorized.
     * This verifies that basic HTTP authentication is enabled for all endpoints.
     */
    @Test
    public void testUnauthenticatedRequestsAreRejected() throws Exception {
        // Test root endpoint
        mockMvc.perform(get("/"))
                .andExpect(status().isUnauthorized());
        
        // Test another endpoint
        mockMvc.perform(get("/owners"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Test that authenticated requests with valid credentials are allowed.
     * This verifies that basic HTTP authentication is working correctly.
     */
    @Test
    public void testAuthenticatedRequestsAreAllowed() throws Exception {
        // Test with valid credentials (user/password as configured in SecurityConfig)
        mockMvc.perform(get("/")
                .with(httpBasic("user", "password")))
                .andExpect(status().isOk());
    }

    /**
     * Test that authenticated requests with invalid credentials are rejected.
     */
    @Test
    public void testInvalidCredentialsAreRejected() throws Exception {
        // Test with invalid credentials
        mockMvc.perform(get("/")
                .with(httpBasic("user", "wrongpassword")))
                .andExpect(status().isUnauthorized());
        
        mockMvc.perform(get("/")
                .with(httpBasic("wronguser", "password")))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Test that requests with mock user annotation work (for testing purposes).
     */
    @Test
    @WithMockUser
    public void testWithMockUserAnnotation() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }
}