package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class SpringSecurityDependencyTest {

    @Test
    public void shouldHaveSpringSecurityStarterOnClasspath() {
        // Test that Spring Security starter classes are available on classpath
        try {
            Class.forName("org.springframework.security.config.annotation.web.configuration.EnableWebSecurity");
            Class.forName("org.springframework.security.web.SecurityFilterChain");
            Class.forName("org.springframework.security.config.annotation.web.builders.HttpSecurity");
            // If we reach here, Spring Security is on the classpath
            assertThat(true).isTrue();
        } catch (ClassNotFoundException e) {
            throw new AssertionError("Spring Security classes not found on classpath. spring-boot-starter-security dependency missing.", e);
        }
    }
}