package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Test to verify Spring Boot 3.x upgrade is successful
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
class SpringBootUpgradeTest {

    @Test
    void contextLoads() {
        // This test will pass if the Spring context loads successfully with Spring Boot 3.x
        // It verifies that all dependencies are compatible and the application starts
    }
}