package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Minimal test to verify Spring Boot 3.x context loads
 */
@SpringBootTest(classes = Application.class)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:hsqldb:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.activemq.broker-url=vm://localhost?broker.persistent=false",
    "logging.level.org.springframework=WARN",
    "logging.level.org.hibernate=WARN"
})
public class SpringBoot3ContextTest {

    @Test
    public void contextLoads() {
        // This test will pass if the Spring Boot 3.x context loads successfully
        // No assertions needed - if context fails to load, the test will fail
    }
}