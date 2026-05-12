package org.springframework.samples.petclinic.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@SpringJUnitConfig
public class SecurityConfigTest {

    @Autowired(required = false)
    private SecurityConfig securityConfig;

    @Test
    public void securityConfigShouldExist() {
        assertThat(securityConfig).isNotNull();
    }

    @Test
    public void securityConfigShouldHaveEnableWebSecurityAnnotation() {
        assertThat(SecurityConfig.class.isAnnotationPresent(EnableWebSecurity.class)).isTrue();
    }
}