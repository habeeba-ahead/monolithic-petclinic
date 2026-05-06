package org.springframework.samples.petclinic.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SecurityDependencyTest {

    @Test
    public void testSpringSecurityDependencyIsAvailable() {
        // Test that Spring Security classes are available on the classpath
        assertDoesNotThrow(() -> {
            Class.forName("org.springframework.security.config.annotation.web.configuration.EnableWebSecurity");
            Class.forName("org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter");
            Class.forName("org.springframework.security.config.annotation.web.builders.HttpSecurity");
        });
    }

    @Test
    public void testSpringSecurityTestDependencyIsAvailable() {
        // Test that Spring Security Test classes are available on the classpath
        assertDoesNotThrow(() -> {
            Class.forName("org.springframework.security.test.context.support.WithMockUser");
            Class.forName("org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders");
        });
    }
}