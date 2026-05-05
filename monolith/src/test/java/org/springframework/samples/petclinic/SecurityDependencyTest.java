package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import static org.assertj.core.api.Assertions.assertThat;

public class SecurityDependencyTest {

    @Test
    public void springSecurityDependencyIsAvailable() {
        // Test that Spring Security classes are available on classpath
        assertThat(EnableWebSecurity.class).isNotNull();
        assertThat(WebSecurityConfigurerAdapter.class).isNotNull();
    }
}