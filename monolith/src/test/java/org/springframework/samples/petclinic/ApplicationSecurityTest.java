package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

/**
 * Test to verify that the application starts successfully with Spring Security enabled.
 * This test ensures that the security configuration doesn't break the application startup.
 */
@SpringBootTest
@SpringJUnitConfig
class ApplicationSecurityTest {

    @Test
    void contextLoadsWithSecurity() {
        // This test will pass if the Spring context loads successfully
        // with the Spring Security configuration in place.
        // If there are any configuration errors, this test will fail.
    }
}