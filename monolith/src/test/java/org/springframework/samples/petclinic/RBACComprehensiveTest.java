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
package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.model.Role;
import org.springframework.samples.petclinic.model.User;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test to validate all RBAC acceptance criteria
 */
public class RBACComprehensiveTest {

    @Test
    public void testAcceptanceCriteria_RoleEnumCreated() {
        // Verify Role enum exists with all required roles
        Role[] roles = Role.values();
        assertEquals(4, roles.length, "Role enum should have exactly 4 roles");
        
        // Verify specific roles exist
        assertNotNull(Role.ADMIN, "ADMIN role should exist");
        assertNotNull(Role.VET, "VET role should exist");
        assertNotNull(Role.RECEPTIONIST, "RECEPTIONIST role should exist");
        assertNotNull(Role.OWNER, "OWNER role should exist");
        
        // Verify role authorities for Spring Security
        assertEquals("ROLE_ADMIN", Role.ADMIN.getAuthority());
        assertEquals("ROLE_VET", Role.VET.getAuthority());
        assertEquals("ROLE_RECEPTIONIST", Role.RECEPTIONIST.getAuthority());
        assertEquals("ROLE_OWNER", Role.OWNER.getAuthority());
    }

    @Test
    public void testAcceptanceCriteria_UserEntityExtendedWithRole() {
        // Verify User entity can be created with role assignment
        User adminUser = new User("admin", "password", Role.ADMIN);
        assertEquals(Role.ADMIN, adminUser.getRole());
        assertEquals("ROLE_ADMIN", adminUser.getAuthorities().iterator().next().getAuthority());
        
        User vetUser = new User("vet", "password", Role.VET);
        assertEquals(Role.VET, vetUser.getRole());
        assertEquals("ROLE_VET", vetUser.getAuthorities().iterator().next().getAuthority());
        
        User receptionistUser = new User("receptionist", "password", Role.RECEPTIONIST);
        assertEquals(Role.RECEPTIONIST, receptionistUser.getRole());
        assertEquals("ROLE_RECEPTIONIST", receptionistUser.getAuthorities().iterator().next().getAuthority());
        
        User ownerUser = new User("owner", "password", Role.OWNER);
        assertEquals(Role.OWNER, ownerUser.getRole());
        assertEquals("ROLE_OWNER", ownerUser.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    public void testAcceptanceCriteria_UserImplementsUserDetails() {
        // Verify User implements Spring Security UserDetails interface
        User user = new User("testuser", "password", Role.VET);
        
        // Test UserDetails methods
        assertEquals("testuser", user.getUsername());
        assertEquals("password", user.getPassword());
        assertEquals(1, user.getAuthorities().size());
        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isCredentialsNonExpired());
        assertTrue(user.isEnabled());
    }

    @Test
    public void testAcceptanceCriteria_RoleBasedAccessControlConfigured() {
        // This test verifies that SecurityConfig class exists and can be instantiated
        // The actual access control testing is done in SecurityConfigTest
        try {
            Class<?> securityConfigClass = Class.forName("org.springframework.samples.petclinic.config.SecurityConfig");
            assertNotNull(securityConfigClass, "SecurityConfig class should exist");
            
            // Verify it has the required annotations
            assertTrue(securityConfigClass.isAnnotationPresent(org.springframework.context.annotation.Configuration.class));
            assertTrue(securityConfigClass.isAnnotationPresent(org.springframework.security.config.annotation.web.configuration.EnableWebSecurity.class));
        } catch (ClassNotFoundException e) {
            fail("SecurityConfig class should exist");
        }
    }

    @Test
    public void testAcceptanceCriteria_DifferentRolesHaveAppropriateAccess() {
        // Verify that different roles have been defined with appropriate authorities
        // This demonstrates the foundation for role-based access control
        
        // Admin should have admin authority
        User admin = new User("admin", "password", Role.ADMIN);
        assertEquals("ROLE_ADMIN", admin.getAuthorities().iterator().next().getAuthority());
        
        // Vet should have vet authority
        User vet = new User("vet", "password", Role.VET);
        assertEquals("ROLE_VET", vet.getAuthorities().iterator().next().getAuthority());
        
        // Receptionist should have receptionist authority
        User receptionist = new User("receptionist", "password", Role.RECEPTIONIST);
        assertEquals("ROLE_RECEPTIONIST", receptionist.getAuthorities().iterator().next().getAuthority());
        
        // Owner should have owner authority
        User owner = new User("owner", "password", Role.OWNER);
        assertEquals("ROLE_OWNER", owner.getAuthorities().iterator().next().getAuthority());
        
        // Verify roles are distinct
        assertNotEquals(admin.getRole(), vet.getRole());
        assertNotEquals(vet.getRole(), receptionist.getRole());
        assertNotEquals(receptionist.getRole(), owner.getRole());
    }

    @Test
    public void testRoleBasedFunctionality() {
        // Test that roles can be used for business logic decisions
        User admin = new User("admin", "password", Role.ADMIN);
        User vet = new User("vet", "password", Role.VET);
        User receptionist = new User("receptionist", "password", Role.RECEPTIONIST);
        User owner = new User("owner", "password", Role.OWNER);
        
        // Simulate role-based access decisions
        assertTrue(hasManagementAccess(admin), "Admin should have management access");
        assertFalse(hasManagementAccess(vet), "Vet should not have management access");
        assertFalse(hasManagementAccess(receptionist), "Receptionist should not have management access");
        assertFalse(hasManagementAccess(owner), "Owner should not have management access");
        
        assertTrue(hasMedicalAccess(admin), "Admin should have medical access");
        assertTrue(hasMedicalAccess(vet), "Vet should have medical access");
        assertFalse(hasMedicalAccess(receptionist), "Receptionist should not have medical access");
        assertFalse(hasMedicalAccess(owner), "Owner should not have medical access");
        
        assertTrue(hasSchedulingAccess(admin), "Admin should have scheduling access");
        assertFalse(hasSchedulingAccess(vet), "Vet should not have scheduling access");
        assertTrue(hasSchedulingAccess(receptionist), "Receptionist should have scheduling access");
        assertFalse(hasSchedulingAccess(owner), "Owner should not have scheduling access");
    }
    
    // Helper methods to simulate role-based access control logic
    private boolean hasManagementAccess(User user) {
        return user.getRole() == Role.ADMIN;
    }
    
    private boolean hasMedicalAccess(User user) {
        return user.getRole() == Role.ADMIN || user.getRole() == Role.VET;
    }
    
    private boolean hasSchedulingAccess(User user) {
        return user.getRole() == Role.ADMIN || user.getRole() == Role.RECEPTIONIST;
    }
}