package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test to verify Spring Security dependency is properly added
 * and application starts without security errors.
 */
@SpringBootTest
class SecurityDependencyTest {

    @Test
    void contextLoads() {
        // This test verifies that the Spring Boot application context loads successfully
        // with Spring Security dependency and configuration in place.
        // If Spring Security dependency was missing or misconfigured, this test would fail.
    }

    @Test
    void springSecurityClassesAreAvailable() {
        // Verify that Spring Security classes are available on the classpath
        try {
            Class.forName("org.springframework.security.config.annotation.web.configuration.EnableWebSecurity");
            Class.forName("org.springframework.security.config.annotation.web.builders.HttpSecurity");
            Class.forName("org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter");
        } catch (ClassNotFoundException e) {
            throw new AssertionError("Spring Security classes not found on classpath", e);
        }
    }

    @Test
    void enableWebSecurityAnnotationIsAvailable() {
        // Verify @EnableWebSecurity annotation is available
        assertThat(EnableWebSecurity.class).isNotNull();
    }
}