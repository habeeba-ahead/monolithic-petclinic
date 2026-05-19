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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for User entity
 */
class UserTests {

    @Test
    void testUserCreation() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setRole(Role.RECEPTIONIST);
        user.setEnabled(true);

        assertThat(user.getUsername()).isEqualTo("testuser");
        assertThat(user.getPassword()).isEqualTo("password");
        assertThat(user.getRole()).isEqualTo(Role.RECEPTIONIST);
        assertThat(user.isEnabled()).isTrue();
    }

    @Test
    void testUserDefaultEnabled() {
        User user = new User();
        assertThat(user.isEnabled()).isTrue();
    }

    @Test
    void testRoleEnum() {
        assertThat(Role.ADMIN).isNotNull();
        assertThat(Role.VETERINARIAN).isNotNull();
        assertThat(Role.RECEPTIONIST).isNotNull();
        
        assertThat(Role.values()).hasSize(3);
    }
}