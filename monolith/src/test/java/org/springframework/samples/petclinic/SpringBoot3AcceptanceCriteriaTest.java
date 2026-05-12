package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test to verify all Spring Boot 3.x upgrade acceptance criteria
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:hsqldb:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.activemq.broker-url=vm://localhost?broker.persistent=false",
    "logging.level.org.springframework=WARN",
    "logging.level.org.hibernate=WARN"
})
public class SpringBoot3AcceptanceCriteriaTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void verifySpringBoot3Version() {
        String version = SpringBootVersion.getVersion();
        assertNotNull(version, "Spring Boot version should not be null");
        assertTrue(version.startsWith("3."), 
                  "Spring Boot version should be 3.x, but was: " + version);
        System.out.println("✓ Spring Boot version: " + version);
    }

    @Test
    public void verifyJava17OrHigher() {
        String javaVersion = System.getProperty("java.version");
        int majorVersion = Integer.parseInt(javaVersion.split("\\.")[0]);
        assertTrue(majorVersion >= 17,
                  "Java version should be 17 or higher for Spring Boot 3.x, but was: " + javaVersion);
        System.out.println("✓ Java version: " + javaVersion);
    }

    @Test
    public void verifyApplicationStartsSuccessfully() {
        assertNotNull(applicationContext, "Application context should not be null");
        if (applicationContext instanceof ConfigurableApplicationContext) {
            assertTrue(((ConfigurableApplicationContext) applicationContext).isActive(), 
                      "Application context should be active");
        }
        System.out.println("✓ Application starts successfully with Spring Boot 3.x");
    }

    @Test
    public void verifyWebFunctionalityWorks() {
        // Test home page
        ResponseEntity<String> homeResponse = restTemplate.getForEntity("http://localhost:" + port + "/", String.class);
        assertEquals(HttpStatus.OK, homeResponse.getStatusCode());
        assertTrue(homeResponse.getBody().contains("Welcome"), "Home page should contain welcome message");
        
        // Test owners functionality
        ResponseEntity<String> ownersResponse = restTemplate.getForEntity("http://localhost:" + port + "/owners/find", String.class);
        assertEquals(HttpStatus.OK, ownersResponse.getStatusCode());
        
        // Test vets functionality
        ResponseEntity<String> vetsResponse = restTemplate.getForEntity("http://localhost:" + port + "/vets", String.class);
        assertEquals(HttpStatus.OK, vetsResponse.getStatusCode());
        
        System.out.println("✓ Web functionality works correctly");
    }

    @Test
    public void verifyDataAccessFunctionalityWorks() {
        // Test that JPA repositories are working
        ResponseEntity<String> ownersResponse = restTemplate.getForEntity("http://localhost:" + port + "/owners", String.class);
        assertEquals(HttpStatus.OK, ownersResponse.getStatusCode());
        
        ResponseEntity<String> vetsResponse = restTemplate.getForEntity("http://localhost:" + port + "/vets", String.class);
        assertEquals(HttpStatus.OK, vetsResponse.getStatusCode());
        
        System.out.println("✓ Data access functionality works correctly");
    }

    @Test
    public void verifyActuatorEndpointsWork() {
        // Test health endpoint
        ResponseEntity<String> healthResponse = restTemplate.getForEntity("http://localhost:" + port + "/manage/health", String.class);
        assertEquals(HttpStatus.OK, healthResponse.getStatusCode());
        assertTrue(healthResponse.getBody().contains("UP"), "Health endpoint should return UP status");
        
        System.out.println("✓ Actuator endpoints work correctly");
    }

    @Test
    public void verifyThymeleafTemplatingWorks() {
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        // Check for Thymeleaf-specific content
        String body = response.getBody();
        assertTrue(body.contains("<!DOCTYPE html>"), "Response should contain HTML DOCTYPE");
        assertTrue(body.contains("Welcome"), "Response should contain welcome message");
        
        System.out.println("✓ Thymeleaf templating works correctly");
    }

    @Test
    public void verifyJMSFunctionalityAvailable() {
        // Verify that JMS beans are available in the context
        assertTrue(applicationContext.containsBean("jmsTemplate"), "JMS template should be available");
        System.out.println("✓ JMS functionality is available");
    }
}