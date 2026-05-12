package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test to verify Spring Boot 3.x upgrade is successful
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:hsqldb:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.activemq.broker-url=vm://localhost?broker.persistent=false"
})
public class SpringBoot3IntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void contextLoads() {
        // This test will pass if the Spring Boot 3.x context loads successfully
        assertTrue(true, "Spring Boot 3.x context should load successfully");
    }

    @Test
    public void homePageLoads() {
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Welcome"), "Home page should contain welcome message");
    }

    @Test
    public void ownersPageLoads() {
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/owners/find", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void vetsPageLoads() {
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/vets", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void actuatorHealthEndpointWorks() {
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/manage/health", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("UP"), "Health endpoint should return UP status");
    }
}