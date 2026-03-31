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
package org.springframework.samples.petclinic.model;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for User entity
 */
public class UserTest {

    @Test
    public void testUserCreation() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setRole(Role.VET);
        user.setEnabled(true);

        assertEquals("testuser", user.getUsername());
        assertEquals("password", user.getPassword());
        assertEquals(Role.VET, user.getRole());
        assertTrue(user.isEnabled());
    }

    @Test
    public void testUserAuthorities() {
        User user = new User();
        user.setRole(Role.ADMIN);

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        assertEquals(1, authorities.size());
        assertEquals("ROLE_ADMIN", authorities.iterator().next().getAuthority());
    }

    @Test
    public void testUserAccountStatus() {
        User user = new User();
        user.setEnabled(true);

        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isCredentialsNonExpired());
        assertTrue(user.isEnabled());
    }

    @Test
    public void testUserWithDifferentRoles() {
        User vetUser = new User();
        vetUser.setRole(Role.VET);
        assertEquals("ROLE_VET", vetUser.getAuthorities().iterator().next().getAuthority());

        User ownerUser = new User();
        ownerUser.setRole(Role.OWNER);
        assertEquals("ROLE_OWNER", ownerUser.getAuthorities().iterator().next().getAuthority());

        User receptionistUser = new User();
        receptionistUser.setRole(Role.RECEPTIONIST);
        assertEquals("ROLE_RECEPTIONIST", receptionistUser.getAuthorities().iterator().next().getAuthority());
    }
}