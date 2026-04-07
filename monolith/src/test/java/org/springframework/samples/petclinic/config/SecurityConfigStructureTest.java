package org.springframework.samples.petclinic.config;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests to verify the structure and annotations of SecurityConfig class
 * without requiring a full Spring application context.
 */
public class SecurityConfigStructureTest {

    @Test
    public void securityConfigShouldHaveConfigurationAnnotation() {
        // Verify @Configuration annotation is present
        Configuration configAnnotation = SecurityConfig.class.getAnnotation(Configuration.class);
        assertThat(configAnnotation).isNotNull();
    }

    @Test
    public void securityConfigShouldHaveEnableWebSecurityAnnotation() {
        // Verify @EnableWebSecurity annotation is present
        EnableWebSecurity enableWebSecurityAnnotation = SecurityConfig.class.getAnnotation(EnableWebSecurity.class);
        assertThat(enableWebSecurityAnnotation).isNotNull();
    }

    @Test
    public void securityConfigShouldExtendWebSecurityConfigurerAdapter() {
        // Verify SecurityConfig extends WebSecurityConfigurerAdapter
        Class<?> superClass = SecurityConfig.class.getSuperclass();
        assertThat(superClass).isEqualTo(WebSecurityConfigurerAdapter.class);
    }

    @Test
    public void securityConfigShouldHavePasswordEncoderBeanMethod() throws Exception {
        // Verify passwordEncoder method exists and has @Bean annotation
        Method passwordEncoderMethod = SecurityConfig.class.getMethod("passwordEncoder");
        assertThat(passwordEncoderMethod).isNotNull();
        assertThat(passwordEncoderMethod.getReturnType()).isEqualTo(PasswordEncoder.class);
        
        Bean beanAnnotation = passwordEncoderMethod.getAnnotation(Bean.class);
        assertThat(beanAnnotation).isNotNull();
    }

    @Test
    public void securityConfigShouldHaveConfigureHttpSecurityMethod() throws Exception {
        // Verify configure(HttpSecurity) method exists and is protected
        Method configureMethod = SecurityConfig.class.getDeclaredMethod("configure", 
            org.springframework.security.config.annotation.web.builders.HttpSecurity.class);
        assertThat(configureMethod).isNotNull();
        assertThat(Modifier.isProtected(configureMethod.getModifiers())).isTrue();
    }

    @Test
    public void passwordEncoderMethodShouldReturnBCryptPasswordEncoder() {
        // Test that the passwordEncoder method returns BCryptPasswordEncoder
        SecurityConfig securityConfig = new SecurityConfig();
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();
        
        assertThat(passwordEncoder).isNotNull();
        assertThat(passwordEncoder).isInstanceOf(BCryptPasswordEncoder.class);
    }

    @Test
    public void bCryptPasswordEncoderShouldWorkCorrectly() {
        // Test that BCrypt password encoder functions correctly
        SecurityConfig securityConfig = new SecurityConfig();
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();
        
        String rawPassword = "testPassword123";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        
        assertThat(encodedPassword).isNotNull();
        assertThat(encodedPassword).isNotEqualTo(rawPassword);
        assertThat(passwordEncoder.matches(rawPassword, encodedPassword)).isTrue();
        assertThat(passwordEncoder.matches("wrongPassword", encodedPassword)).isFalse();
    }

    @Test
    public void securityConfigClassShouldBePublic() {
        // Verify SecurityConfig class is public
        assertThat(Modifier.isPublic(SecurityConfig.class.getModifiers())).isTrue();
    }
}