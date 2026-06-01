package org.springframework.samples.petclinic.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestPropertySource(
    locations = "classpath:application-test.properties"
)
public class SecurityConfigTest {

    @Test
    public void contextLoads() {
        // This test verifies that the Spring context loads successfully with Spring Security enabled
        // If this test passes, it means the application starts successfully with Spring Security
        assertTrue(true, "Spring context loaded successfully with Spring Security");
    }

    @Test
    public void securityConfigHasEnableWebSecurityAnnotation() {
        // Verify that SecurityConfig class has @EnableWebSecurity annotation
        EnableWebSecurity annotation = SecurityConfig.class.getAnnotation(EnableWebSecurity.class);
        assertNotNull(annotation, "SecurityConfig should have @EnableWebSecurity annotation");
    }
}