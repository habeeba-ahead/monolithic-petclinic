package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.Visit;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test to verify Jakarta EE migration is successful
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class JakartaMigrationTest {

    @Test
    void jakartaValidationWorks() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        // Test validation on Owner entity
        Owner owner = new Owner();
        // Leave required fields empty to trigger validation errors
        
        Set<ConstraintViolation<Owner>> violations = validator.validate(owner);
        
        // Should have validation errors for firstName, lastName, address, city, telephone
        assertThat(violations).hasSizeGreaterThan(0);
        
        // Verify that jakarta validation annotations are working
        boolean hasFirstNameViolation = violations.stream()
            .anyMatch(v -> "firstName".equals(v.getPropertyPath().toString()));
        assertThat(hasFirstNameViolation).isTrue();
    }

    @Test
    void jakartaPersistenceAnnotationsPresent() {
        // Verify that JPA annotations are present and working
        // This test ensures that the jakarta.persistence annotations are properly loaded
        
        Owner owner = new Owner();
        assertThat(owner.getClass().getAnnotation(jakarta.persistence.Entity.class)).isNotNull();
        assertThat(owner.getClass().getAnnotation(jakarta.persistence.Table.class)).isNotNull();
        
        Pet pet = new Pet();
        assertThat(pet.getClass().getAnnotation(jakarta.persistence.Entity.class)).isNotNull();
        
        Visit visit = new Visit();
        assertThat(visit.getClass().getAnnotation(jakarta.persistence.Entity.class)).isNotNull();
    }
}