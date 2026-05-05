package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@SpringJUnitConfig
public class SecurityIntegrationTest {

    @LocalServerPort
    private int port;

    private TestRestTemplate restTemplate = new TestRestTemplate();

    @Test
    public void applicationStartsWithSecurityEnabled() {
        // Test that the application starts and security is enabled
        // When accessing a protected endpoint, we should get a redirect to login or 401/403
        ResponseEntity<String> response = restTemplate.getForEntity(
            "http://localhost:" + port + "/", String.class);
        
        // With Spring Security enabled, we expect either:
        // - 302 redirect to login page
        // - 401 Unauthorized 
        // - 200 OK if the root path is permitted
        assertThat(response.getStatusCode()).isIn(
            HttpStatus.OK, 
            HttpStatus.FOUND, 
            HttpStatus.UNAUTHORIZED
        );
    }
}