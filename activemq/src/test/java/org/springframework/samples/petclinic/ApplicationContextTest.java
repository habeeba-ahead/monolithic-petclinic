package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Integration test to verify the Spring Boot 2.7.x application context loads correctly
 * for the ActiveMQ module.
 */
@SpringBootTest
@TestPropertySource(properties = {
    "activemq.broker-url=vm://localhost?broker.persistent=false",
    "server.port=0"
})
class ApplicationContextTest {

    @Test
    void contextLoads() {
        // Verifies that the Spring Boot 2.7.x application context starts without errors
    }
}
