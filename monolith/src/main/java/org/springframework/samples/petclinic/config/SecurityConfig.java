package org.springframework.samples.petclinic.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Spring Security configuration class that provides basic form-based authentication
 * and role-based access control for the Pet Clinic application.
 * 
 * This configuration defines three basic user roles:
 * - ADMIN: Full access to all functionality including management features
 * - VET: Access to veterinary functions and patient records
 * - STAFF: Access to general clinic operations like owner and pet management
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * Configure HTTP security including form-based login and role-based access control.
     * 
     * @param http the HttpSecurity to configure
     * @throws Exception if an error occurs during configuration
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                // Allow public access to welcome page and static resources
                .antMatchers("/", "/welcome", "/resources/**", "/webjars/**", "/css/**", "/js/**", "/images/**").permitAll()
                // Admin-only access to management features
                .antMatchers("/management/**").hasRole("ADMIN")
                // Vet access for veterinary functions
                .antMatchers("/vets/**").hasAnyRole("ADMIN", "VET")
                // Staff access for general clinic operations (owners, pets, visits)
                .antMatchers("/owners/**", "/pets/**", "/visits/**").hasAnyRole("ADMIN", "VET", "STAFF")
                // All other requests require authentication
                .anyRequest().authenticated()
                .and()
            .formLogin()
                // Configure custom login page
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .permitAll()
                .and()
            .logout()
                .logoutSuccessUrl("/")
                .permitAll()
                .and()
            .csrf()
                // Disable CSRF for simplicity in this basic configuration
                .disable();
    }

    /**
     * Configure BCrypt password encoder for secure password hashing.
     * BCrypt is a strong hashing function designed for password storage.
     * 
     * @return BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}