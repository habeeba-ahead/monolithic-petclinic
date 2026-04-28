package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Test to verify the application starts successfully with Spring Boot 3.x
 */
@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:hsqldb:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class ApplicationStartupTest {

    @Test
    void contextLoads() {
        // This test will pass if the Spring context loads successfully
        // which verifies that the application can start with Spring Boot 3.x
    }
}