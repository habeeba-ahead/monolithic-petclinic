package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test to verify that all required Spring Security imports are available.
 * This test ensures the spring-boot-starter-security dependency is properly configured.
 */
public class SecurityImportsTest {

    @Test
    public void springSecurityImportsAreAvailable() {
        // Test that all required Spring Security classes can be imported
        try {
            Class.forName("org.springframework.security.config.annotation.web.configuration.EnableWebSecurity");
            Class.forName("org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter");
            Class.forName("org.springframework.context.annotation.Configuration");
            
            // If we reach here, all imports are available
            assertThat(true).isTrue();
        } catch (ClassNotFoundException e) {
            assertThat(false).as("Spring Security classes should be available: " + e.getMessage()).isTrue();
        }
    }
}