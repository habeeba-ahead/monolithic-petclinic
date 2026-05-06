package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.SpringVersion;
import org.springframework.boot.SpringBootVersion;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test to verify that all major dependencies are at secure versions
 */
@SpringBootTest
public class DependencySecurityTest {

    @Test
    public void shouldUseSecureSpringFrameworkVersion() {
        String springVersion = SpringVersion.getVersion();
        System.out.println("Current Spring Framework version: " + springVersion);
        
        // Spring Framework 5.3.x is the secure version for Spring Boot 2.7.x
        assertTrue(springVersion.startsWith("5.3."), 
            "Expected Spring Framework 5.3.x but found: " + springVersion);
    }
    
    @Test
    public void shouldUseSecureSpringBootVersion() {
        String springBootVersion = SpringBootVersion.getVersion();
        System.out.println("Current Spring Boot version: " + springBootVersion);
        
        // Verify we're using Spring Boot 2.7.18 (latest patch version)
        assertTrue(springBootVersion.equals("2.7.18"), 
            "Expected Spring Boot 2.7.18 but found: " + springBootVersion);
    }
    
    @Test
    public void shouldNotUseVulnerableVersions() {
        String springBootVersion = SpringBootVersion.getVersion();
        
        // Ensure we're not using known vulnerable versions
        assertTrue(!springBootVersion.startsWith("2.2."), 
            "Should not be using Spring Boot 2.2.x versions due to security vulnerabilities");
        assertTrue(!springBootVersion.startsWith("2.3."), 
            "Should not be using Spring Boot 2.3.x versions due to security vulnerabilities");
        assertTrue(!springBootVersion.startsWith("2.4."), 
            "Should not be using Spring Boot 2.4.x versions due to security vulnerabilities");
        assertTrue(!springBootVersion.startsWith("2.5."), 
            "Should not be using Spring Boot 2.5.x versions due to security vulnerabilities");
        assertTrue(!springBootVersion.startsWith("2.6."), 
            "Should not be using Spring Boot 2.6.x versions due to security vulnerabilities");
    }
}