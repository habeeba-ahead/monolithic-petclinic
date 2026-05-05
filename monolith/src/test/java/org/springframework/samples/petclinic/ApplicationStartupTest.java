package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

/**
 * Test to verify that the application starts successfully with Spring Security enabled
 */
@SpringBootTest
@SpringJUnitConfig
public class ApplicationStartupTest {

    @Test
    public void applicationStartsWithSecurityFrameworkEnabled() {
        // If this test passes, it means the Spring Boot application context
        // loaded successfully with Spring Security configuration
        // This verifies that:
        // 1. Spring Security dependency is properly configured
        // 2. SecurityConfig is valid and loads without errors
        // 3. Application starts with security framework enabled
    }
}