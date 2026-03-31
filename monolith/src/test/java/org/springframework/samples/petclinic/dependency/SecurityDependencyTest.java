package org.springframework.samples.petclinic.dependency;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SecurityDependencyTest {

    @Test
    public void testSpringSecurityClassesAvailable() {
        // Test that Spring Security classes are available on classpath
        assertDoesNotThrow(() -> {
            Class.forName("org.springframework.security.config.annotation.web.configuration.EnableWebSecurity");
            Class.forName("org.springframework.security.web.SecurityFilterChain");
            Class.forName("org.springframework.security.core.userdetails.UserDetailsService");
            Class.forName("org.springframework.security.crypto.password.PasswordEncoder");
        }, "Spring Security classes should be available on classpath");
    }

    @Test
    public void testSpringBootSecurityStarterAvailable() {
        // Test that Spring Boot Security starter classes are available
        assertDoesNotThrow(() -> {
            Class.forName("org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration");
        }, "Spring Boot Security starter classes should be available on classpath");
    }
}