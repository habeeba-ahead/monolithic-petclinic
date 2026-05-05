package org.springframework.samples.petclinic.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Basic Spring Security configuration for the Pet Clinic application.
 * This configuration enables the Spring Security framework with default settings.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    // Basic configuration - using Spring Security defaults
    // This will enable form-based authentication with default login page
    
}