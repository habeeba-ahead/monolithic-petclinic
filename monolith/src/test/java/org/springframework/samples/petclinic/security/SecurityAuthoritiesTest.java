package org.springframework.samples.petclinic.security;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for SecurityAuthorities constants
 */
public class SecurityAuthoritiesTest {

    @Test
    public void shouldHaveUserAuthorities() {
        assertThat(SecurityAuthorities.USER_READ_PETS).isEqualTo("user:read:pets");
        assertThat(SecurityAuthorities.USER_WRITE_PETS).isEqualTo("user:write:pets");
        assertThat(SecurityAuthorities.USER_READ_VISITS).isEqualTo("user:read:visits");
        assertThat(SecurityAuthorities.USER_WRITE_VISITS).isEqualTo("user:write:visits");
    }

    @Test
    public void shouldHaveVetAuthorities() {
        assertThat(SecurityAuthorities.VET_READ_ALL_PETS).isEqualTo("vet:read:all_pets");
        assertThat(SecurityAuthorities.VET_READ_ALL_VISITS).isEqualTo("vet:read:all_visits");
        assertThat(SecurityAuthorities.VET_WRITE_VISITS).isEqualTo("vet:write:visits");
        assertThat(SecurityAuthorities.VET_READ_OWNERS).isEqualTo("vet:read:owners");
    }

    @Test
    public void shouldHaveAdminAuthorities() {
        assertThat(SecurityAuthorities.ADMIN_READ_ALL).isEqualTo("admin:read:all");
        assertThat(SecurityAuthorities.ADMIN_WRITE_ALL).isEqualTo("admin:write:all");
        assertThat(SecurityAuthorities.ADMIN_DELETE_ALL).isEqualTo("admin:delete:all");
        assertThat(SecurityAuthorities.ADMIN_MANAGE_USERS).isEqualTo("admin:manage:users");
    }

    @Test
    public void shouldHaveRoleBasedAuthorityArrays() {
        String[] userAuthorities = SecurityAuthorities.getUserAuthorities();
        assertThat(userAuthorities).containsExactly(
            SecurityAuthorities.USER_READ_PETS,
            SecurityAuthorities.USER_WRITE_PETS,
            SecurityAuthorities.USER_READ_VISITS,
            SecurityAuthorities.USER_WRITE_VISITS
        );

        String[] vetAuthorities = SecurityAuthorities.getVetAuthorities();
        assertThat(vetAuthorities).containsExactly(
            SecurityAuthorities.VET_READ_ALL_PETS,
            SecurityAuthorities.VET_READ_ALL_VISITS,
            SecurityAuthorities.VET_WRITE_VISITS,
            SecurityAuthorities.VET_READ_OWNERS
        );

        String[] adminAuthorities = SecurityAuthorities.getAdminAuthorities();
        assertThat(adminAuthorities).containsExactly(
            SecurityAuthorities.ADMIN_READ_ALL,
            SecurityAuthorities.ADMIN_WRITE_ALL,
            SecurityAuthorities.ADMIN_DELETE_ALL,
            SecurityAuthorities.ADMIN_MANAGE_USERS
        );
    }
}