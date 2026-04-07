package org.springframework.samples.petclinic.config;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

public class SecurityConfigTest {

    @Test
    public void securityConfigShouldHaveConfigurationAnnotation() {
        // Test that SecurityConfig class has @Configuration annotation
        Configuration configAnnotation = SecurityConfig.class.getAnnotation(Configuration.class);
        assertThat(configAnnotation).isNotNull();
    }

    @Test
    public void securityConfigShouldHaveEnableWebSecurityAnnotation() {
        // Test that SecurityConfig class has @EnableWebSecurity annotation
        EnableWebSecurity enableWebSecurityAnnotation = SecurityConfig.class.getAnnotation(EnableWebSecurity.class);
        assertThat(enableWebSecurityAnnotation).isNotNull();
    }

    @Test
    public void securityConfigShouldHavePasswordEncoderBean() throws Exception {
        // Test that SecurityConfig has a passwordEncoder method that returns PasswordEncoder
        Method passwordEncoderMethod = SecurityConfig.class.getMethod("passwordEncoder");
        assertThat(passwordEncoderMethod).isNotNull();
        assertThat(passwordEncoderMethod.getReturnType()).isEqualTo(PasswordEncoder.class);
        
        // Test that the method returns BCryptPasswordEncoder
        SecurityConfig securityConfig = new SecurityConfig();
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();
        assertThat(passwordEncoder).isInstanceOf(BCryptPasswordEncoder.class);
    }

    @Test
    public void securityConfigShouldCompileWithoutErrors() {
        // This test will fail if the SecurityConfig class doesn't compile
        SecurityConfig securityConfig = new SecurityConfig();
        assertThat(securityConfig).isNotNull();
    }

    @Test
    public void passwordEncoderShouldEncodePasswords() {
        // Test that the password encoder actually works
        SecurityConfig securityConfig = new SecurityConfig();
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();
        
        String rawPassword = "testPassword";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        
        assertThat(encodedPassword).isNotNull();
        assertThat(encodedPassword).isNotEqualTo(rawPassword);
        assertThat(passwordEncoder.matches(rawPassword, encodedPassword)).isTrue();
    }

    @Test
    public void securityConfigShouldExtendWebSecurityConfigurerAdapter() {
        // Test that SecurityConfig extends WebSecurityConfigurerAdapter
        Class<?> superClass = SecurityConfig.class.getSuperclass();
        assertThat(superClass.getSimpleName()).isEqualTo("WebSecurityConfigurerAdapter");
    }
}