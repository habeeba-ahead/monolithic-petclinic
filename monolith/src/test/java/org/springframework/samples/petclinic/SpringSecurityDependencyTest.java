package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.assertj.core.api.Assertions.assertThat;

public class SpringSecurityDependencyTest {

    @Test
    public void springSecurityClassesAreAvailable() {
        // Test that Spring Security classes are on the classpath
        assertThat(EnableWebSecurity.class).isNotNull();
        assertThat(SecurityFilterChain.class).isNotNull();
    }
}