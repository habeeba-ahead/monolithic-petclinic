package org.springframework.samples.petclinic.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.TestPropertySource;

import java.util.Collection;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test to verify the complete role hierarchy and authority system
 */
@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:hsqldb:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
public class RoleHierarchyIntegrationTest {

    @Autowired
    private RoleHierarchy roleHierarchy;

    @Test
    public void shouldVerifyCompleteRoleHierarchy() {
        // Test ADMIN role has all authorities
        Collection<? extends org.springframework.security.core.GrantedAuthority> adminAuthorities = 
            roleHierarchy.getReachableGrantedAuthorities(
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
            );
        
        assertThat(adminAuthorities).hasSize(3);
        assertThat(adminAuthorities).extracting("authority")
            .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_VET", "ROLE_USER");

        // Test VET role has VET and USER authorities
        Collection<? extends org.springframework.security.core.GrantedAuthority> vetAuthorities = 
            roleHierarchy.getReachableGrantedAuthorities(
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_VET"))
            );
        
        assertThat(vetAuthorities).hasSize(2);
        assertThat(vetAuthorities).extracting("authority")
            .containsExactlyInAnyOrder("ROLE_VET", "ROLE_USER");

        // Test USER role has only USER authority
        Collection<? extends org.springframework.security.core.GrantedAuthority> userAuthorities = 
            roleHierarchy.getReachableGrantedAuthorities(
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
            );
        
        assertThat(userAuthorities).hasSize(1);
        assertThat(userAuthorities).extracting("authority")
            .containsExactly("ROLE_USER");
    }

    @Test
    public void shouldVerifyUserRoleEnumIntegration() {
        // Verify that UserRole enum values work with the hierarchy
        Collection<? extends org.springframework.security.core.GrantedAuthority> adminAuthorities = 
            roleHierarchy.getReachableGrantedAuthorities(
                Collections.singletonList(UserRole.ADMIN)
            );
        
        assertThat(adminAuthorities).hasSize(3);
        assertThat(adminAuthorities).extracting("authority")
            .contains(UserRole.ADMIN.getAuthority(), UserRole.VET.getAuthority(), UserRole.USER.getAuthority());
    }

    @Test
    public void shouldVerifyAuthorityConstants() {
        // Verify that authority constants are properly defined
        String[] userAuthorities = SecurityAuthorities.getUserAuthorities();
        String[] vetAuthorities = SecurityAuthorities.getVetAuthorities();
        String[] adminAuthorities = SecurityAuthorities.getAdminAuthorities();

        assertThat(userAuthorities).hasSize(4);
        assertThat(vetAuthorities).hasSize(4);
        assertThat(adminAuthorities).hasSize(4);

        // Verify no overlap in authority names (each role has distinct permissions)
        assertThat(userAuthorities).allMatch(auth -> auth.startsWith("user:"));
        assertThat(vetAuthorities).allMatch(auth -> auth.startsWith("vet:"));
        assertThat(adminAuthorities).allMatch(auth -> auth.startsWith("admin:"));
    }
}