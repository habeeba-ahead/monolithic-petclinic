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
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.Validation;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for User entity
 */
@SpringBootTest
class UserTests {

    private ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private Validator validator = factory.getValidator();

    @Test
    void testUserCreation() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setEnabled(true);
        user.setRoles("ROLE_USER");

        assertThat(user.getUsername()).isEqualTo("testuser");
        assertThat(user.getPassword()).isEqualTo("password123");
        assertThat(user.isEnabled()).isTrue();
        assertThat(user.getRoles()).isEqualTo("ROLE_USER");
    }

    @Test
    void testUserValidation() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setEnabled(true);
        user.setRoles("ROLE_USER");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).isEmpty();
    }

    @Test
    void testUserValidationWithEmptyUsername() {
        User user = new User();
        user.setUsername("");
        user.setPassword("password123");
        user.setEnabled(true);
        user.setRoles("ROLE_USER");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("must not be empty");
    }

    @Test
    void testUserValidationWithEmptyPassword() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("");
        user.setEnabled(true);
        user.setRoles("ROLE_USER");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("must not be empty");
    }

    @Test
    void testUserValidationWithEmptyRoles() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setEnabled(true);
        user.setRoles("");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("must not be empty");
    }

    @Test
    void testUserInheritsFromBaseEntity() {
        User user = new User();
        assertThat(user.getId()).isNull();
        assertThat(user.isNew()).isTrue();

        user.setId(1);
        assertThat(user.getId()).isEqualTo(1);
        assertThat(user.isNew()).isFalse();
    }
}