/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Security configuration for the Pet Clinic application.
 * Implements role-based access control (RBAC) for different user types.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                // Public endpoints - accessible to all
                .antMatchers("/", "/welcome", "/resources/**", "/webjars/**", "/css/**", "/js/**", "/images/**").permitAll()
                
                // Admin-only endpoints
                .antMatchers("/management/**").hasRole("ADMIN")
                
                // Vet-specific endpoints
                .antMatchers("/vets/**").hasAnyRole("ADMIN", "VET", "RECEPTIONIST")
                
                // Owner management - accessible to admin, receptionist, and vets
                .antMatchers("/owners/new", "/owners/*/edit").hasAnyRole("ADMIN", "RECEPTIONIST")
                .antMatchers("/owners/**").hasAnyRole("ADMIN", "VET", "RECEPTIONIST", "OWNER")
                
                // Pet management - accessible to admin, vets, and receptionists
                .antMatchers("/owners/*/pets/new", "/owners/*/pets/*/edit").hasAnyRole("ADMIN", "VET", "RECEPTIONIST")
                .antMatchers("/owners/*/pets/**").hasAnyRole("ADMIN", "VET", "RECEPTIONIST", "OWNER")
                
                // Visit management - accessible to admin and vets
                .antMatchers("/owners/*/pets/*/visits/new").hasAnyRole("ADMIN", "VET")
                .antMatchers("/owners/*/pets/*/visits/**").hasAnyRole("ADMIN", "VET", "OWNER")
                
                // All other requests require authentication
                .anyRequest().authenticated()
            .and()
            .formLogin()
                .loginPage("/login")
                .permitAll()
                .defaultSuccessUrl("/", true)
            .and()
            .logout()
                .permitAll()
                .logoutSuccessUrl("/")
            .and()
            .csrf().disable(); // Disable CSRF for simplicity in this example
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}