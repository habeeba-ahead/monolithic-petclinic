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
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Role enum
 */
public class RoleTest {

    @Test
    public void testRoleEnumValues() {
        // Test that all required roles exist
        assertEquals(4, Role.values().length);
        
        // Test specific role values
        assertEquals("ADMIN", Role.ADMIN.name());
        assertEquals("VET", Role.VET.name());
        assertEquals("RECEPTIONIST", Role.RECEPTIONIST.name());
        assertEquals("OWNER", Role.OWNER.name());
    }

    @Test
    public void testRoleFromString() {
        // Test that we can get roles from string values
        assertEquals(Role.ADMIN, Role.valueOf("ADMIN"));
        assertEquals(Role.VET, Role.valueOf("VET"));
        assertEquals(Role.RECEPTIONIST, Role.valueOf("RECEPTIONIST"));
        assertEquals(Role.OWNER, Role.valueOf("OWNER"));
    }

    @Test
    public void testRoleAuthority() {
        // Test that roles have proper authority strings for Spring Security
        assertEquals("ROLE_ADMIN", Role.ADMIN.getAuthority());
        assertEquals("ROLE_VET", Role.VET.getAuthority());
        assertEquals("ROLE_RECEPTIONIST", Role.RECEPTIONIST.getAuthority());
        assertEquals("ROLE_OWNER", Role.OWNER.getAuthority());
    }
}