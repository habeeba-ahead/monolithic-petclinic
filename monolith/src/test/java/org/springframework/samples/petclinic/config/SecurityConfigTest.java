package org.springframework.samples.petclinic.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureWebMvc
@SpringJUnitConfig
public class SecurityConfigTest {

    @Autowired(required = false)
    private SecurityConfig securityConfig;

    @Autowired(required = false)
    private SecurityFilterChain securityFilterChain;

    @Test
    public void securityConfigShouldExist() {
        assertThat(securityConfig).isNotNull();
    }

    @Test
    public void securityFilterChainShouldBeConfigured() {
        assertThat(securityFilterChain).isNotNull();
    }
}