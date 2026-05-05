package org.springframework.samples.petclinic.config;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for SecurityConfig class structure and annotations.
 * These tests verify the class is properly configured without requiring Spring context.
 */
public class SecurityConfigStructureTest {

    @Test
    public void securityConfigHasConfigurationAnnotation() {
        assertThat(SecurityConfig.class.isAnnotationPresent(Configuration.class)).isTrue();
    }

    @Test
    public void securityConfigHasEnableWebSecurityAnnotation() {
        assertThat(SecurityConfig.class.isAnnotationPresent(EnableWebSecurity.class)).isTrue();
    }

    @Test
    public void securityConfigExtendsWebSecurityConfigurerAdapter() {
        assertThat(WebSecurityConfigurerAdapter.class.isAssignableFrom(SecurityConfig.class)).isTrue();
    }

    @Test
    public void securityConfigCanBeInstantiated() {
        SecurityConfig config = new SecurityConfig();
        assertThat(config).isNotNull();
        assertThat(config).isInstanceOf(WebSecurityConfigurerAdapter.class);
    }
}