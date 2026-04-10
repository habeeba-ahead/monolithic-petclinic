/*
 * Test to verify Spring Boot 2.7.x validation functionality
 */
package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.test.context.TestPropertySource;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.Validation;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test to verify that Spring Boot 2.7.x validation dependency is working correctly.
 * In Spring Boot 2.3+, spring-boot-starter-validation is required for Bean Validation.
 */
@SpringBootTest
@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
class SpringBoot27ValidationTest {

    @Test
    void shouldValidateOwnerWithValidationStarter() {
        // This test verifies that the spring-boot-starter-validation dependency
        // is properly configured for Spring Boot 2.7.x
        
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        
        Owner owner = new Owner();
        // Set invalid data - empty first name should trigger validation error
        owner.setFirstName("");
        owner.setLastName("Smith");
        owner.setAddress("123 Main St");
        owner.setCity("Springfield");
        owner.setTelephone("1234567890"); // Valid 10-digit number
        
        Set<ConstraintViolation<Owner>> violations = validator.validate(owner);
        
        // Should have validation error for empty firstName
        assertThat(violations).hasSize(1);
        ConstraintViolation<Owner> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("firstName");
        assertThat(violation.getMessage()).isEqualTo("must not be empty");
    }
    
    @Test
    void shouldPassValidationWithValidOwner() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        
        Owner owner = new Owner();
        owner.setFirstName("John");
        owner.setLastName("Smith");
        owner.setAddress("123 Main St");
        owner.setCity("Springfield");
        owner.setTelephone("1234567890"); // Valid 10-digit number
        
        Set<ConstraintViolation<Owner>> violations = validator.validate(owner);
        
        // Should have no validation errors
        assertThat(violations).isEmpty();
    }
    
    @Test
    void shouldValidateInvalidTelephoneNumber() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        
        Owner owner = new Owner();
        owner.setFirstName("John");
        owner.setLastName("Smith");
        owner.setAddress("123 Main St");
        owner.setCity("Springfield");
        owner.setTelephone("555-1234"); // Invalid format (contains dashes)
        
        Set<ConstraintViolation<Owner>> violations = validator.validate(owner);
        
        // Should have validation error for invalid telephone format
        assertThat(violations).hasSize(1);
        ConstraintViolation<Owner> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("telephone");
        assertThat(violation.getMessage()).contains("numeric value out of bounds");
    }
}