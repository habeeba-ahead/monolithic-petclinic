package org.springframework.samples.petclinic.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/", "/welcome", "/resources/**", "/webjars/**", "/css/**", "/js/**", "/images/**").permitAll()
                .antMatchers("/vets", "/vets.html").permitAll()
                .anyRequest().authenticated()
            .and()
            .oauth2Login()
                .loginPage("/oauth2/authorization/google")
                .defaultSuccessUrl("/", true)
            .and()
            .logout()
                .logoutSuccessUrl("/")
                .permitAll();
    }
}