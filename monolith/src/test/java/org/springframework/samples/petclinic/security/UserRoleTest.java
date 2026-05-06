package org.springframework.samples.petclinic.security;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for UserRole enum
 */
public class UserRoleTest {

    @Test
    public void shouldHaveUserRole() {
        UserRole role = UserRole.USER;
        assertThat(role).isNotNull();
        assertThat(role.name()).isEqualTo("USER");
    }

    @Test
    public void shouldHaveVetRole() {
        UserRole role = UserRole.VET;
        assertThat(role).isNotNull();
        assertThat(role.name()).isEqualTo("VET");
    }

    @Test
    public void shouldHaveAdminRole() {
        UserRole role = UserRole.ADMIN;
        assertThat(role).isNotNull();
        assertThat(role.name()).isEqualTo("ADMIN");
    }

    @Test
    public void shouldHaveCorrectNumberOfRoles() {
        UserRole[] roles = UserRole.values();
        assertThat(roles).hasSize(3);
    }

    @Test
    public void shouldReturnCorrectAuthority() {
        assertThat(UserRole.USER.getAuthority()).isEqualTo("ROLE_USER");
        assertThat(UserRole.VET.getAuthority()).isEqualTo("ROLE_VET");
        assertThat(UserRole.ADMIN.getAuthority()).isEqualTo("ROLE_ADMIN");
    }
}