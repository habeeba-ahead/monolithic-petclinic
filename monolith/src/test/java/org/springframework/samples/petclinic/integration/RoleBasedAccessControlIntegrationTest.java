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
package org.springframework.samples.petclinic.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.samples.petclinic.db.UserRepository;
import org.springframework.samples.petclinic.model.Role;
import org.springframework.samples.petclinic.model.User;
import org.springframework.samples.petclinic.service.UserDetailsServiceImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for Role-Based Access Control functionality
 */
@SpringBootTest
@SpringJUnitConfig
@Transactional
public class RoleBasedAccessControlIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void testCompleteRBACFlow() {
        // Test creating users with different roles
        User adminUser = new User("admin_test", passwordEncoder.encode("password"), Role.ADMIN);
        User vetUser = new User("vet_test", passwordEncoder.encode("password"), Role.VET);
        User receptionistUser = new User("receptionist_test", passwordEncoder.encode("password"), Role.RECEPTIONIST);
        User ownerUser = new User("owner_test", passwordEncoder.encode("password"), Role.OWNER);

        // Save users
        userRepository.save(adminUser);
        userRepository.save(vetUser);
        userRepository.save(receptionistUser);
        userRepository.save(ownerUser);

        // Test user authentication and role assignment
        UserDetails adminDetails = userDetailsService.loadUserByUsername("admin_test");
        assertEquals("ROLE_ADMIN", adminDetails.getAuthorities().iterator().next().getAuthority());
        assertTrue(passwordEncoder.matches("password", adminDetails.getPassword()));

        UserDetails vetDetails = userDetailsService.loadUserByUsername("vet_test");
        assertEquals("ROLE_VET", vetDetails.getAuthorities().iterator().next().getAuthority());

        UserDetails receptionistDetails = userDetailsService.loadUserByUsername("receptionist_test");
        assertEquals("ROLE_RECEPTIONIST", receptionistDetails.getAuthorities().iterator().next().getAuthority());

        UserDetails ownerDetails = userDetailsService.loadUserByUsername("owner_test");
        assertEquals("ROLE_OWNER", ownerDetails.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    public void testRoleEnumIntegration() {
        // Test that Role enum works correctly with database persistence
        User user = new User("role_test", "password", Role.VET);
        User savedUser = userRepository.save(user);

        User retrievedUser = userRepository.findById(savedUser.getId()).orElse(null);
        assertNotNull(retrievedUser);
        assertEquals(Role.VET, retrievedUser.getRole());
        assertEquals("ROLE_VET", retrievedUser.getRole().getAuthority());
    }

    @Test
    public void testUserAccountStatus() {
        // Test that user account status methods work correctly
        User user = new User("status_test", "password", Role.OWNER);
        user.setEnabled(true);
        User savedUser = userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername("status_test");
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.isEnabled());
    }

    @Test
    public void testPasswordEncoding() {
        // Test that password encoding works correctly
        String rawPassword = "testPassword123";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        
        User user = new User("password_test", encodedPassword, Role.ADMIN);
        userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername("password_test");
        assertTrue(passwordEncoder.matches(rawPassword, userDetails.getPassword()));
        assertFalse(passwordEncoder.matches("wrongPassword", userDetails.getPassword()));
    }
}