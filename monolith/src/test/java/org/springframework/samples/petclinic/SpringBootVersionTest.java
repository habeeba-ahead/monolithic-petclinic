package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringBootVersion;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test to verify Spring Boot version is 3.2.x
 */
public class SpringBootVersionTest {

    @Test
    public void shouldUseSpringBoot3_2_x() {
        String version = SpringBootVersion.getVersion();
        System.out.println("Current Spring Boot version: " + version);
        
        // Verify we're using Spring Boot 3.2.x
        assertTrue(version.startsWith("3.2."), 
            "Expected Spring Boot version 3.2.x but found: " + version);
    }
}