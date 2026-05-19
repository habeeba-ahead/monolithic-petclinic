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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * Security configuration for role-based access control
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                // Management endpoints restricted to admin role only
                .antMatchers("/management/**").hasRole("ADMIN")
                // Veterinarian endpoints accessible to vet and admin roles
                .antMatchers("/vets/**").hasAnyRole("VETERINARIAN", "ADMIN", "RECEPTIONIST")
                // Public endpoints accessible to all authenticated users
                .antMatchers("/owners/**", "/pets/**", "/visits/**").authenticated()
                // Static resources and welcome page
                .antMatchers("/resources/**", "/webjars/**", "/", "/welcome").permitAll()
                .anyRequest().authenticated()
            .and()
            .httpBasic()
            .and()
            .csrf().disable(); // Disable CSRF for simplicity in this demo
    }

    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        UserDetails admin = User.builder()
            .username("admin")
            .password(passwordEncoder().encode("admin"))
            .roles("ADMIN")
            .build();

        UserDetails vet = User.builder()
            .username("vet")
            .password(passwordEncoder().encode("vet"))
            .roles("VETERINARIAN")
            .build();

        UserDetails receptionist = User.builder()
            .username("receptionist")
            .password(passwordEncoder().encode("receptionist"))
            .roles("RECEPTIONIST")
            .build();

        return new InMemoryUserDetailsManager(admin, vet, receptionist);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}