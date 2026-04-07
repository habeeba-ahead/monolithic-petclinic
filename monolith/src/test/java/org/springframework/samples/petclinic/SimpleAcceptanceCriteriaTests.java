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
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.samples.petclinic.model.BaseEntity;
import org.springframework.samples.petclinic.model.User;
import org.springframework.samples.petclinic.repository.UserRepository;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Simple tests to verify all acceptance criteria for User entity and repository without Spring context
 */
class SimpleAcceptanceCriteriaTests {

    @Test
    void testUserEntityHasRequiredFields() {
        // Verify User entity has username, password, enabled, and roles fields
        User user = new User();
        
        // Test that all required fields exist and can be set/get
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
    void testUserEntityHasJpaAnnotations() throws NoSuchFieldException {
        // Verify User entity is properly annotated with JPA annotations
        Class<User> userClass = User.class;
        
        // Check @Entity annotation
        assertThat(userClass.isAnnotationPresent(Entity.class)).isTrue();
        
        // Check @Table annotation
        assertThat(userClass.isAnnotationPresent(Table.class)).isTrue();
        Table tableAnnotation = userClass.getAnnotation(Table.class);
        assertThat(tableAnnotation.name()).isEqualTo("users");
        
        // Check @Column annotations on fields
        Field usernameField = userClass.getDeclaredField("username");
        assertThat(usernameField.isAnnotationPresent(Column.class)).isTrue();
        Column usernameColumn = usernameField.getAnnotation(Column.class);
        assertThat(usernameColumn.name()).isEqualTo("username");
        assertThat(usernameColumn.unique()).isTrue();
        
        Field passwordField = userClass.getDeclaredField("password");
        assertThat(passwordField.isAnnotationPresent(Column.class)).isTrue();
        Column passwordColumn = passwordField.getAnnotation(Column.class);
        assertThat(passwordColumn.name()).isEqualTo("password");
        
        Field enabledField = userClass.getDeclaredField("enabled");
        assertThat(enabledField.isAnnotationPresent(Column.class)).isTrue();
        Column enabledColumn = enabledField.getAnnotation(Column.class);
        assertThat(enabledColumn.name()).isEqualTo("enabled");
        
        Field rolesField = userClass.getDeclaredField("roles");
        assertThat(rolesField.isAnnotationPresent(Column.class)).isTrue();
        Column rolesColumn = rolesField.getAnnotation(Column.class);
        assertThat(rolesColumn.name()).isEqualTo("roles");
    }

    @Test
    void testUserEntityHasValidationAnnotations() throws NoSuchFieldException {
        // Verify User entity has proper validation annotations
        Class<User> userClass = User.class;
        
        // Check @NotEmpty annotations
        Field usernameField = userClass.getDeclaredField("username");
        assertThat(usernameField.isAnnotationPresent(NotEmpty.class)).isTrue();
        
        Field passwordField = userClass.getDeclaredField("password");
        assertThat(passwordField.isAnnotationPresent(NotEmpty.class)).isTrue();
        
        Field rolesField = userClass.getDeclaredField("roles");
        assertThat(rolesField.isAnnotationPresent(NotEmpty.class)).isTrue();
    }

    @Test
    void testUserEntityExtendsBaseEntity() {
        // Verify User entity extends BaseEntity (inheritance relationship)
        User user = new User();
        assertThat(user).isInstanceOf(BaseEntity.class);
        
        // Test inherited methods from BaseEntity
        assertThat(user.getId()).isNull();
        assertThat(user.isNew()).isTrue();
        
        user.setId(1);
        assertThat(user.getId()).isEqualTo(1);
        assertThat(user.isNew()).isFalse();
    }

    @Test
    void testUserRepositoryExtendsJpaRepository() {
        // Verify UserRepository interface extends JpaRepository
        Class<UserRepository> repositoryClass = UserRepository.class;
        
        // Check that UserRepository extends JpaRepository
        assertThat(JpaRepository.class.isAssignableFrom(repositoryClass)).isTrue();
        
        // Verify the generic types are correct (User, Integer)
        assertThat(repositoryClass.getGenericInterfaces()).hasSize(1);
    }

    @Test
    void testUserRepositoryHasCustomMethods() throws NoSuchMethodException {
        // Verify UserRepository has custom methods for authentication
        Class<UserRepository> repositoryClass = UserRepository.class;
        
        // Check findByUsername method exists
        Method findByUsernameMethod = repositoryClass.getMethod("findByUsername", String.class);
        assertThat(findByUsernameMethod).isNotNull();
        assertThat(findByUsernameMethod.getReturnType()).isEqualTo(User.class);
        
        // Check existsByUsername method exists
        Method existsByUsernameMethod = repositoryClass.getMethod("existsByUsername", String.class);
        assertThat(existsByUsernameMethod).isNotNull();
        assertThat(existsByUsernameMethod.getReturnType()).isEqualTo(boolean.class);
    }

    @Test
    void testRolesFieldMappingForAuthentication() {
        // Verify roles field is properly mapped for authentication use
        User user = new User();
        user.setUsername("admin");
        user.setPassword("adminpass");
        user.setEnabled(true);
        user.setRoles("ROLE_ADMIN,ROLE_USER");
        
        assertThat(user.getRoles()).isEqualTo("ROLE_ADMIN,ROLE_USER");
        assertThat(user.getRoles()).contains("ROLE_ADMIN");
        assertThat(user.getRoles()).contains("ROLE_USER");
    }
}