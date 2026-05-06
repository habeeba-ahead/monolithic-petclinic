package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Test to verify the application starts successfully with Spring Boot 2.7.x
 */
@SpringBootTest
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.datasource.url=jdbc:hsqldb:mem:testdb",
    "spring.jms.embedded.enabled=false"
})
public class ApplicationStartupTest {

    @Test
    public void contextLoads() {
        // This test will pass if the Spring context loads successfully
        // which verifies that the Spring Boot 2.7.x upgrade is working
    }
}