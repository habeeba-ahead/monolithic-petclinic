package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Simple test to verify Spring Boot 3.x upgrade without web context
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource(locations = "classpath:application.properties")
class SimpleSpringBootTest {

    @Test
    void verifySpringBootVersion() {
        // Verify we're running Spring Boot 3.x
        String version = SpringBootVersion.getVersion();
        assertThat(version).startsWith("3.");
    }

    @Test
    void contextLoads() {
        // This test will pass if the Spring context loads successfully with Spring Boot 3.x
        // It verifies that all dependencies are compatible and the application starts
        assertThat(true).isTrue();
    }

    @Test
    void verifyJakartaEEMigration() {
        // Verify Jakarta EE packages are available (Spring Boot 3.x requirement)
        try {
            Class.forName("jakarta.persistence.Entity");
            Class.forName("jakarta.validation.constraints.NotNull");
            Class.forName("jakarta.servlet.http.HttpServlet");
        } catch (ClassNotFoundException e) {
            throw new AssertionError("Jakarta EE dependencies not found - Spring Boot 3.x migration incomplete", e);
        }
    }
}