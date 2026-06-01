package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.samples.petclinic.config.SecurityConfig;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test to verify Spring Security configuration and dependencies
 */
@SpringBootTest
@TestPropertySource(
    locations = "classpath:application-test.properties"
)
public class SpringSecurityIntegrationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void contextLoadsWithSpringSecurity() {
        // Verify that the Spring context loads successfully with Spring Security enabled
        assertNotNull(applicationContext, "Application context should load successfully");
        
        // Verify SecurityConfig bean is present
        SecurityConfig securityConfig = applicationContext.getBean(SecurityConfig.class);
        assertNotNull(securityConfig, "SecurityConfig bean should be present in context");
    }

    @Test
    public void securityConfigHasCorrectAnnotations() {
        // Verify that SecurityConfig class has @EnableWebSecurity annotation
        EnableWebSecurity annotation = SecurityConfig.class.getAnnotation(EnableWebSecurity.class);
        assertNotNull(annotation, "SecurityConfig should have @EnableWebSecurity annotation");
    }

    @Test
    public void securityFilterChainBeanExists() {
        // Verify that SecurityFilterChain bean is configured
        SecurityFilterChain filterChain = applicationContext.getBean(SecurityFilterChain.class);
        assertNotNull(filterChain, "SecurityFilterChain bean should be present in context");
    }

    @Test
    public void springSecurityClassesAreAvailable() {
        // Verify that Spring Security classes are available on the classpath
        assertDoesNotThrow(() -> {
            Class.forName("org.springframework.security.config.annotation.web.configuration.EnableWebSecurity");
            Class.forName("org.springframework.security.web.SecurityFilterChain");
            Class.forName("org.springframework.security.config.annotation.web.builders.HttpSecurity");
            Class.forName("org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration");
        }, "Spring Security classes should be available on classpath");
    }
}