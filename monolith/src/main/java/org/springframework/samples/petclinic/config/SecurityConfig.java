package org.springframework.samples.petclinic.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Basic Spring Security configuration for the Pet Clinic application.
 * This configuration enables web security and sets up basic HTTP security.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * Configure HTTP security with basic settings.
     * Currently allows all requests to pass through without authentication.
     * This provides the foundation for future security enhancements.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .anyRequest().permitAll()
                .and()
            .csrf().disable()
            .headers().frameOptions().deny();
    }
}