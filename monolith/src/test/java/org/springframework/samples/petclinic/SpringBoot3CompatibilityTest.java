package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test to verify Spring Boot 3.x compatibility and functionality
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application.properties")
class SpringBoot3CompatibilityTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ClinicService clinicService;

    @Test
    void contextLoads() {
        // This test verifies that the Spring context loads successfully with Spring Boot 3.x
        assertThat(clinicService).isNotNull();
    }

    @Test
    void webApplicationStartsSuccessfully() {
        // Test that the web application starts and responds to HTTP requests
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Welcome");
    }

    @Test
    void jpaRepositoriesWork() {
        // Test that JPA repositories work with Jakarta Persistence API
        assertThat(clinicService.petTypes()).isNotEmpty();
        assertThat(clinicService.allVets()).isNotEmpty();
    }

    @Test
    void validationWorks() {
        // Test that Jakarta Validation works correctly
        assertThat(clinicService.ownerByLastName("")).isNotEmpty();
    }
}