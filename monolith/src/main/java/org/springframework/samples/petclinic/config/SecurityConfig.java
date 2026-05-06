package org.springframework.samples.petclinic.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;

/**
 * Security configuration for the Pet Clinic application.
 * Configures role hierarchy and basic security settings.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * Configures the role hierarchy where ADMIN > VET > USER.
     * This means:
     * - ADMIN users inherit all VET and USER authorities
     * - VET users inherit all USER authorities
     * - USER users have only their own authorities
     */
    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy("ROLE_ADMIN > ROLE_VET > ROLE_USER");
        return hierarchy;
    }

    /**
     * Configures HTTP security.
     * For now, permits all requests to allow gradual security implementation.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .anyRequest().permitAll()
            .and()
            .csrf().disable()
            .headers().frameOptions().disable();
    }
}