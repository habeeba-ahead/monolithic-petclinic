package org.springframework.samples.petclinic.actuator;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Integration test to verify that the application starts correctly with Actuator enabled.
 */
@SpringBootTest
@TestPropertySource(properties = {
    "management.endpoints.web.exposure.include=health,info"
})
class ActuatorIntegrationTests {

    @Test
    void contextLoads() {
        // This test will pass if the Spring context loads successfully with Actuator
    }
}