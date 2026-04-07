package org.springframework.samples.petclinic.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class SecurityConfigIntegrationTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void passwordEncoderBeanShouldBeAvailable() {
        // Test that the password encoder bean is properly configured
        assertThat(passwordEncoder).isNotNull();
        assertThat(passwordEncoder.getClass().getSimpleName()).isEqualTo("BCryptPasswordEncoder");
    }

    @Test
    public void passwordEncoderShouldWorkCorrectly() {
        // Test that the password encoder functions correctly
        String rawPassword = "testPassword123";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        
        assertThat(encodedPassword).isNotNull();
        assertThat(encodedPassword).isNotEqualTo(rawPassword);
        assertThat(passwordEncoder.matches(rawPassword, encodedPassword)).isTrue();
        assertThat(passwordEncoder.matches("wrongPassword", encodedPassword)).isFalse();
    }
}