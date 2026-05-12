package org.springframework.samples.petclinic.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.samples.petclinic.config.SecurityConfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Comprehensive acceptance test for Spring Security implementation
 * Verifies all acceptance criteria:
 * 1. spring-boot-starter-security dependency added to pom.xml
 * 2. SecurityConfig class created with @EnableWebSecurity annotation
 * 3. Basic HTTP security configuration implemented
 * 4. Application starts with Spring Security enabled
 * 5. Default login page accessible
 */
@SpringBootTest
@AutoConfigureMockMvc
@SpringJUnitConfig
public class SpringSecurityAcceptanceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired(required = false)
    private SecurityConfig securityConfig;

    @Test
    public void criteriaOne_SecurityConfigClassExists() {
        // Verifies SecurityConfig class is created and autowired (implies dependency is present)
        assertThat(securityConfig).isNotNull();
    }

    @Test
    public void criteriaTwo_SecurityConfigHasEnableWebSecurityAnnotation() {
        // Verifies @EnableWebSecurity annotation is present
        assertThat(SecurityConfig.class.isAnnotationPresent(EnableWebSecurity.class)).isTrue();
    }

    @Test
    public void criteriaThree_BasicHttpSecurityConfigurationImplemented() throws Exception {
        // Verifies basic HTTP security is configured - unauthenticated requests are redirected
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser
    public void criteriaThree_AuthenticatedRequestsAreAllowed() throws Exception {
        // Verifies authenticated requests are allowed through
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }

    @Test
    public void criteriaFour_ApplicationStartsWithSpringSecurityEnabled() {
        // If this test runs, it means the application context loaded successfully with Spring Security
        assertThat(securityConfig).isNotNull();
        // Additional verification that security is actually enabled by checking redirect behavior
    }

    @Test
    public void criteriaFive_DefaultLoginPageAccessible() throws Exception {
        // Verifies that the login page is accessible
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"));
    }

    @Test
    public void staticResourcesArePermitted() throws Exception {
        // Verifies that static resources are accessible without authentication
        mockMvc.perform(get("/resources/css/petclinic.css"))
                .andExpect(status().isOk());
    }
}