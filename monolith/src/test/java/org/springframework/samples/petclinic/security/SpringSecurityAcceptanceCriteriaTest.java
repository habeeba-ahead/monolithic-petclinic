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
 * Test to verify all Spring Security acceptance criteria are met
 */
@SpringBootTest
@AutoConfigureMockMvc
@SpringJUnitConfig
public class SpringSecurityAcceptanceCriteriaTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired(required = false)
    private SecurityConfig securityConfig;

    @Test
    public void criteria1_SpringBootStarterSecurityDependencyAdded() {
        // If SecurityConfig can be autowired, it means the dependency is present and working
        assertThat(securityConfig).isNotNull();
    }

    @Test
    public void criteria2_SecurityConfigClassCreatedWithEnableWebSecurityAnnotation() {
        // Verify SecurityConfig class exists and has @EnableWebSecurity annotation
        assertThat(securityConfig).isNotNull();
        assertThat(SecurityConfig.class.isAnnotationPresent(EnableWebSecurity.class)).isTrue();
    }

    @Test
    public void criteria3_BasicHttpSecurityConfigurationImplemented() throws Exception {
        // Verify that unauthenticated requests are redirected to login
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser
    public void criteria3_AuthenticatedRequestsAllowed() throws Exception {
        // Verify that authenticated requests are allowed
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }

    @Test
    public void criteria4_ApplicationStartsWithSpringSecurityEnabled() {
        // If this test runs, the application context loaded successfully with Spring Security
        // Additional verification that security is enabled
        assertThat(securityConfig).isNotNull();
    }

    @Test
    public void criteria5_DefaultLoginPageAccessible() throws Exception {
        // Verify that the default Spring Security login page is accessible
        // Spring Security provides a default login page at /login when form login is enabled
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Please sign in")));
    }

    @Test
    public void staticResourcesPermittedWithoutAuthentication() throws Exception {
        // Verify that static resources are accessible without authentication
        mockMvc.perform(get("/resources/css/petclinic.css"))
                .andExpect(status().isOk());
    }
}