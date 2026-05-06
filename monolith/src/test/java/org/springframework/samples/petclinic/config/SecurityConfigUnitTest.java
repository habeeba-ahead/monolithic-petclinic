package org.springframework.samples.petclinic.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for SecurityConfig without Spring context
 */
public class SecurityConfigUnitTest {

    @Test
    public void shouldCreateRoleHierarchyBean() {
        SecurityConfig securityConfig = new SecurityConfig();
        RoleHierarchy roleHierarchy = securityConfig.roleHierarchy();
        
        assertThat(roleHierarchy).isNotNull();
    }

    @Test
    public void shouldConfigureCorrectRoleHierarchy() {
        SecurityConfig securityConfig = new SecurityConfig();
        RoleHierarchy roleHierarchy = securityConfig.roleHierarchy();
        
        // Test that ADMIN has VET authorities
        boolean adminHasVetRole = roleHierarchy.getReachableGrantedAuthorities(
            java.util.Collections.singletonList(
                new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_ADMIN")
            )
        ).stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_VET"));
        
        assertThat(adminHasVetRole).isTrue();

        // Test that VET has USER authorities  
        boolean vetHasUserRole = roleHierarchy.getReachableGrantedAuthorities(
            java.util.Collections.singletonList(
                new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_VET")
            )
        ).stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_USER"));
        
        assertThat(vetHasUserRole).isTrue();

        // Test that USER doesn't have VET authorities
        boolean userHasVetRole = roleHierarchy.getReachableGrantedAuthorities(
            java.util.Collections.singletonList(
                new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_USER")
            )
        ).stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_VET"));
        
        assertThat(userHasVetRole).isFalse();
    }
}