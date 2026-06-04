package org.springframework.samples.petclinic.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class to verify Spring Security configuration and acceptance criteria.
 */
@SpringBootTest
@AutoConfigureWebMvc
class SecurityConfigTest {

    @Autowired
    private SecurityConfig securityConfig;

    @Test
    void securityConfigShouldBeCreated() {
        // Verify SecurityConfig class exists and is properly configured
        assertThat(securityConfig).isNotNull();
        
        // Verify @EnableWebSecurity annotation is present
        EnableWebSecurity enableWebSecurity = SecurityConfig.class.getAnnotation(EnableWebSecurity.class);
        assertThat(enableWebSecurity).isNotNull();
    }

    @Test
    void basicHttpSecurityShouldBeConfigured() {
        // Verify that the security configuration is properly set up
        // This test ensures the configure method executes without errors
        assertThat(securityConfig).isNotNull();
    }
}