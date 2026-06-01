package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.config.SecurityConfig;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SecurityIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private SecurityConfig securityConfig;

    @Test
    public void applicationStartsSuccessfullyWithSpringSecurity() {
        // Test that the application context loads successfully
        assertThat(securityConfig).isNotNull();
        assertThat(SecurityConfig.class.isAnnotationPresent(EnableWebSecurity.class)).isTrue();
    }

    @Test
    public void welcomePageIsAccessibleWithoutAuthentication() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/", String.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void ownersPageIsAccessibleWithoutAuthentication() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/owners", String.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}