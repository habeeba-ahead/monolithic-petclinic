package org.springframework.samples.petclinic.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for SecurityConfig
 */
@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:hsqldb:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
public class SecurityConfigTest {

    @Autowired(required = false)
    private RoleHierarchy roleHierarchy;

    @Test
    public void shouldHaveRoleHierarchyBean() {
        assertThat(roleHierarchy).isNotNull();
    }

    @Test
    public void shouldConfigureRoleHierarchy() {
        if (roleHierarchy != null) {
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
        }
    }
}