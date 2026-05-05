package org.springframework.samples.petclinic.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@SpringJUnitConfig
public class SecurityConfigTest {

    @Test
    public void contextLoads(ApplicationContext context) {
        // Test that SecurityConfig bean is created
        assertThat(context.getBeansOfType(SecurityConfig.class)).hasSize(1);
    }

    @Test
    public void securityConfigHasEnableWebSecurityAnnotation() {
        // Test that SecurityConfig class has @EnableWebSecurity annotation
        assertThat(SecurityConfig.class.isAnnotationPresent(EnableWebSecurity.class)).isTrue();
    }

    @Test
    public void securityConfigClassExists() {
        // Test that SecurityConfig class exists and can be instantiated
        assertThat(SecurityConfig.class).isNotNull();
        SecurityConfig config = new SecurityConfig();
        assertThat(config).isNotNull();
    }
}