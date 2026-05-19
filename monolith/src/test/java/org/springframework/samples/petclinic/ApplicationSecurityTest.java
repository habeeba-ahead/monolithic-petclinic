package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

/**
 * Test to verify that the application starts successfully with Spring Security enabled.
 */
@SpringBootTest
@SpringJUnitConfig
public class ApplicationSecurityTest {

    @Test
    public void contextLoadsWithSecurity() {
        // This test verifies that the Spring application context loads successfully
        // with Spring Security configuration enabled
    }
}