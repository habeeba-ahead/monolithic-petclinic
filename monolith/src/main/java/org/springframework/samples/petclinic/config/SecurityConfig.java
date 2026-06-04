package org.springframework.samples.petclinic.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
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
                .roles("VET")
                .build();

        UserDetails owner = User.builder()
                .username("owner")
                .password(passwordEncoder().encode("owner"))
                .roles("OWNER")
                .build();

        return new InMemoryUserDetailsManager(admin, vet, owner);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/resources/**", "/webjars/**", "/css/**", "/js/**", "/images/**").permitAll()
                .antMatchers("/login").permitAll()
                .antMatchers("/management/**").hasRole("ADMIN")
                .antMatchers("/vets/**").hasAnyRole("ADMIN", "VET")
                .antMatchers("/owners/**").hasAnyRole("ADMIN", "OWNER")
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error=true")
                .permitAll()
                .and()
            .logout()
                .logoutSuccessUrl("/login?logout=true")
                .permitAll()
                .and()
            .csrf().disable(); // Disable CSRF for simplicity in this basic implementation
    }
}