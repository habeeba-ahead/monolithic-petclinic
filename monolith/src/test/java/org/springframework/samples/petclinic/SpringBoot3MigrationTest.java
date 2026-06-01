package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test to verify successful migration to Spring Boot 3.x
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
class SpringBoot3MigrationTest {

    @Test
    void verifySpringBootVersion() {
        // Verify we're running Spring Boot 3.x
        String version = SpringBootVersion.getVersion();
        assertThat(version).startsWith("3.");
    }

    @Test
    void contextLoadsWithSpringBoot3() {
        // This test will only pass if the application context loads successfully
        // with Spring Boot 3.x and all Jakarta EE migrations are correct
        assertThat(true).isTrue(); // Context loading itself is the test
    }
}