package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
@TestPropertySource(
    locations = "classpath:application-test.properties"
)
public class DependencyVerificationTest {

    @Test
    public void springSecurityDependencyIsAvailable() {
        // This test verifies that Spring Security classes are available on the classpath
        // If this test passes, it means the spring-boot-starter-security dependency was added correctly
        assertDoesNotThrow(() -> {
            Class.forName("org.springframework.security.config.annotation.web.configuration.EnableWebSecurity");
            Class.forName("org.springframework.security.web.SecurityFilterChain");
            Class.forName("org.springframework.security.config.annotation.web.builders.HttpSecurity");
        }, "Spring Security classes should be available on classpath");
    }
}