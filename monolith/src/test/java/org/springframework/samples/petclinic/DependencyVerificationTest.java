package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test to verify that Spring Security dependencies are properly available on the classpath.
 */
public class DependencyVerificationTest {

    @Test
    public void springSecurityClassesAreAvailable() {
        // Verify that key Spring Security classes are available on the classpath
        // This confirms that spring-boot-starter-security dependency is properly added
        
        try {
            Class<?> enableWebSecurityClass = Class.forName("org.springframework.security.config.annotation.web.configuration.EnableWebSecurity");
            assertThat(enableWebSecurityClass).isNotNull();
            
            Class<?> securityFilterChainClass = Class.forName("org.springframework.security.web.SecurityFilterChain");
            assertThat(securityFilterChainClass).isNotNull();
            
            Class<?> httpSecurityClass = Class.forName("org.springframework.security.config.annotation.web.builders.HttpSecurity");
            assertThat(httpSecurityClass).isNotNull();
            
        } catch (ClassNotFoundException e) {
            throw new AssertionError("Spring Security classes not found on classpath. Dependency may not be properly added.", e);
        }
    }

    @Test
    public void springSecurityAnnotationsWork() {
        // Verify that Spring Security annotations are properly loaded
        EnableWebSecurity annotation = EnableWebSecurity.class.getAnnotation(EnableWebSecurity.class);
        // If we can access the annotation class, the dependency is working
        assertThat(EnableWebSecurity.class).isNotNull();
    }
}