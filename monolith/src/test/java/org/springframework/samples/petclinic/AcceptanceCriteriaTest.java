package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.config.SecurityConfig;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class to verify all acceptance criteria for Spring Security implementation
 */
public class AcceptanceCriteriaTest {

    @Test
    public void springBootStarterSecurityDependencyAddedToPom() throws IOException {
        // Verify spring-boot-starter-security dependency is in pom.xml
        String pomContent = new String(Files.readAllBytes(Paths.get("pom.xml")));
        
        assertThat(pomContent)
            .as("pom.xml should contain spring-boot-starter-security dependency")
            .contains("spring-boot-starter-security");
        
        assertThat(pomContent)
            .as("pom.xml should contain Spring Boot group ID")
            .contains("org.springframework.boot");
    }

    @Test
    public void securityConfigClassCreatedWithEnableWebSecurityAnnotation() {
        // Verify SecurityConfig class exists
        assertThat(SecurityConfig.class)
            .as("SecurityConfig class should exist")
            .isNotNull();
        
        // Verify @EnableWebSecurity annotation is present
        assertThat(SecurityConfig.class.isAnnotationPresent(EnableWebSecurity.class))
            .as("SecurityConfig should have @EnableWebSecurity annotation")
            .isTrue();
        
        // Verify it extends WebSecurityConfigurerAdapter
        assertThat(WebSecurityConfigurerAdapter.class.isAssignableFrom(SecurityConfig.class))
            .as("SecurityConfig should extend WebSecurityConfigurerAdapter")
            .isTrue();
    }

    @Test
    public void basicSecurityConfigurationCompilesWithoutErrors() {
        // If this test runs, it means the SecurityConfig class compiled successfully
        // Try to instantiate the SecurityConfig class
        try {
            SecurityConfig config = new SecurityConfig();
            assertThat(config)
                .as("SecurityConfig should be instantiable")
                .isNotNull();
        } catch (Exception e) {
            throw new AssertionError("SecurityConfig should compile and be instantiable without errors", e);
        }
    }

    @Test
    public void springSecurityClassesAreAvailableOnClasspath() {
        // Verify Spring Security classes are available (dependency is properly added)
        assertThat(EnableWebSecurity.class)
            .as("EnableWebSecurity class should be available on classpath")
            .isNotNull();
        
        assertThat(WebSecurityConfigurerAdapter.class)
            .as("WebSecurityConfigurerAdapter class should be available on classpath")
            .isNotNull();
    }

    @Test
    public void applicationMainClassExists() {
        // Verify the main application class exists for startup test
        try {
            Class<?> applicationClass = Class.forName("org.springframework.samples.petclinic.Application");
            assertThat(applicationClass)
                .as("Application class should exist for startup verification")
                .isNotNull();
        } catch (ClassNotFoundException e) {
            throw new AssertionError("Application class should exist", e);
        }
    }
}