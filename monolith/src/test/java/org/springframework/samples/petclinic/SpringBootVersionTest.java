package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test to verify Spring Boot version upgrade to 2.7.x
 */
@SpringBootTest
public class SpringBootVersionTest {

    @Test
    public void shouldUseSpringBoot27OrHigher() {
        String version = SpringBootVersion.getVersion();
        System.out.println("Current Spring Boot version: " + version);
        
        // Verify we're using Spring Boot 2.7.x
        assertTrue(version.startsWith("2.7."), 
            "Expected Spring Boot version 2.7.x but found: " + version);
    }
    
    @Test
    public void shouldNotUseVulnerableVersion() {
        String version = SpringBootVersion.getVersion();
        
        // Ensure we're not using the vulnerable 2.2.1 version
        assertTrue(!version.startsWith("2.2.1"), 
            "Should not be using vulnerable Spring Boot 2.2.1 version");
    }
}