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
package org.springframework.samples.petclinic.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.samples.petclinic.model.User;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for UserRepository
 */
@SpringBootTest
@Transactional
class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByUsername() {
        // Create and save a test user
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setEnabled(true);
        user.setRoles("ROLE_USER");
        
        userRepository.save(user);

        // Test finding by username
        User foundUser = userRepository.findByUsername("testuser");
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUsername()).isEqualTo("testuser");
        assertThat(foundUser.getPassword()).isEqualTo("password123");
        assertThat(foundUser.isEnabled()).isTrue();
        assertThat(foundUser.getRoles()).isEqualTo("ROLE_USER");
    }

    @Test
    void testFindByUsernameNotFound() {
        User foundUser = userRepository.findByUsername("nonexistent");
        assertThat(foundUser).isNull();
    }

    @Test
    void testSaveUser() {
        User user = new User();
        user.setUsername("newuser");
        user.setPassword("newpassword");
        user.setEnabled(false);
        user.setRoles("ROLE_ADMIN");

        User savedUser = userRepository.save(user);
        
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("newuser");
        assertThat(savedUser.getPassword()).isEqualTo("newpassword");
        assertThat(savedUser.isEnabled()).isFalse();
        assertThat(savedUser.getRoles()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    void testExistsByUsername() {
        // Create and save a test user
        User user = new User();
        user.setUsername("existinguser");
        user.setPassword("password123");
        user.setEnabled(true);
        user.setRoles("ROLE_USER");
        
        userRepository.save(user);

        // Test exists by username
        boolean exists = userRepository.existsByUsername("existinguser");
        assertThat(exists).isTrue();

        boolean notExists = userRepository.existsByUsername("nonexistentuser");
        assertThat(notExists).isFalse();
    }
}