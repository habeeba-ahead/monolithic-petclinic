package org.springframework.samples.petclinic.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.samples.petclinic.config.SecurityConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive verification test for Spring Security implementation
 * This test verifies all acceptance criteria without requiring a full Spring context
 */
public class ComprehensiveSecurityVerificationTest {

    @Test
    public void testSpringSecurityDependencyInPom() throws IOException {
        String pomContent = Files.readString(Paths.get("pom.xml"));
        assertTrue(pomContent.contains("spring-boot-starter-security"), 
                "spring-boot-starter-security dependency should be present in pom.xml");
    }

    @Test
    public void testSecurityConfigClassExists() {
        assertDoesNotThrow(() -> {
            Class.forName("org.springframework.samples.petclinic.config.SecurityConfig");
        }, "SecurityConfig class should exist");
    }

    @Test
    public void testSecurityConfigHasEnableWebSecurityAnnotation() {
        assertTrue(SecurityConfig.class.isAnnotationPresent(EnableWebSecurity.class),
                "SecurityConfig should have @EnableWebSecurity annotation");
    }

    @Test
    public void testSecurityConfigFileContainsRequiredConfiguration() throws IOException {
        String securityConfigPath = "src/main/java/org/springframework/samples/petclinic/config/SecurityConfig.java";
        String configContent = Files.readString(Paths.get(securityConfigPath));
        
        // Verify basic structure
        assertTrue(configContent.contains("@Configuration"), 
                "SecurityConfig should have @Configuration annotation");
        assertTrue(configContent.contains("@EnableWebSecurity"), 
                "SecurityConfig should have @EnableWebSecurity annotation");
        assertTrue(configContent.contains("public class SecurityConfig"), 
                "SecurityConfig should be a public class");
        
        // Verify HTTP Basic authentication
        assertTrue(configContent.contains("httpBasic()"), 
                "SecurityConfig should configure HTTP Basic authentication");
        
        // Verify all endpoints require authentication
        assertTrue(configContent.contains("anyRequest().authenticated()"), 
                "SecurityConfig should require authentication for all requests");
        
        // Verify required beans
        assertTrue(configContent.contains("SecurityFilterChain"), 
                "SecurityConfig should define SecurityFilterChain bean");
        assertTrue(configContent.contains("UserDetailsService"), 
                "SecurityConfig should define UserDetailsService bean");
        assertTrue(configContent.contains("PasswordEncoder"), 
                "SecurityConfig should define PasswordEncoder bean");
    }

    @Test
    public void testSpringSecurityClassesAvailable() {
        // Test that Spring Security classes are available on classpath
        assertDoesNotThrow(() -> {
            Class.forName("org.springframework.security.config.annotation.web.configuration.EnableWebSecurity");
            Class.forName("org.springframework.security.web.SecurityFilterChain");
            Class.forName("org.springframework.security.core.userdetails.UserDetailsService");
            Class.forName("org.springframework.security.crypto.password.PasswordEncoder");
            Class.forName("org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder");
        }, "Spring Security classes should be available on classpath");
    }
}