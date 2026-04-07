package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:hsqldb:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
public class ApplicationStartupTest {

    @Test
    public void applicationStartsSuccessfullyWithSpringSecurityOnClasspath() {
        // This test verifies that the application context loads successfully
        // with Spring Security dependency on the classpath.
        // If Spring Security is properly configured, this test will pass.
        // The @SpringBootTest annotation will start the full application context.
    }
}