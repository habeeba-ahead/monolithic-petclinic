# javax to jakarta Migration Checklist

## Overview
This checklist covers all required changes to migrate from javax.* to jakarta.* packages for Spring Boot 3.x compatibility.

## Pre-Migration Setup

### ✅ Environment Preparation
- [ ] Upgrade to Java 17+ (current: Java 8)
- [ ] Update Maven to 3.9.x (current: 3.6.2)
- [ ] Update IDE to support Jakarta EE 9+
- [ ] Create feature branch for migration
- [ ] Backup current working version

## Package Import Changes

### ✅ JPA/Persistence Imports (28 occurrences)

#### Files to Update:
- [ ] `monolith/src/main/java/org/springframework/samples/petclinic/model/BaseEntity.java`
  ```java
  // BEFORE
  import javax.persistence.GeneratedValue;
  import javax.persistence.GenerationType;
  import javax.persistence.Id;
  import javax.persistence.MappedSuperclass;
  
  // AFTER
  import jakarta.persistence.GeneratedValue;
  import jakarta.persistence.GenerationType;
  import jakarta.persistence.Id;
  import jakarta.persistence.MappedSuperclass;
  ```

- [ ] `monolith/src/main/java/org/springframework/samples/petclinic/model/NamedEntity.java`
  ```java
  // BEFORE
  import javax.persistence.Column;
  import javax.persistence.MappedSuperclass;
  
  // AFTER
  import jakarta.persistence.Column;
  import jakarta.persistence.MappedSuperclass;
  ```

- [ ] `monolith/src/main/java/org/springframework/samples/petclinic/model/Person.java`
  ```java
  // BEFORE
  import javax.persistence.Column;
  import javax.persistence.MappedSuperclass;
  
  // AFTER
  import jakarta.persistence.Column;
  import jakarta.persistence.MappedSuperclass;
  ```

- [ ] `monolith/src/main/java/org/springframework/samples/petclinic/model/Owner.java`
  ```java
  // BEFORE
  import javax.persistence.CascadeType;
  import javax.persistence.Column;
  import javax.persistence.Entity;
  import javax.persistence.OneToMany;
  import javax.persistence.Table;
  
  // AFTER
  import jakarta.persistence.CascadeType;
  import jakarta.persistence.Column;
  import jakarta.persistence.Entity;
  import jakarta.persistence.OneToMany;
  import jakarta.persistence.Table;
  ```

- [ ] `monolith/src/main/java/org/springframework/samples/petclinic/model/Pet.java`
  ```java
  // BEFORE
  import javax.persistence.Column;
  import javax.persistence.Entity;
  import javax.persistence.JoinColumn;
  import javax.persistence.ManyToOne;
  import javax.persistence.Table;
  import javax.persistence.Transient;
  
  // AFTER
  import jakarta.persistence.Column;
  import jakarta.persistence.Entity;
  import jakarta.persistence.JoinColumn;
  import jakarta.persistence.ManyToOne;
  import jakarta.persistence.Table;
  import jakarta.persistence.Transient;
  ```

- [ ] `monolith/src/main/java/org/springframework/samples/petclinic/model/PetType.java`
  ```java
  // BEFORE
  import javax.persistence.Entity;
  import javax.persistence.Table;
  
  // AFTER
  import jakarta.persistence.Entity;
  import jakarta.persistence.Table;
  ```

- [ ] `monolith/src/main/java/org/springframework/samples/petclinic/model/Specialty.java`
  ```java
  // BEFORE
  import javax.persistence.Entity;
  import javax.persistence.Table;
  
  // AFTER
  import jakarta.persistence.Entity;
  import jakarta.persistence.Table;
  ```

- [ ] `monolith/src/main/java/org/springframework/samples/petclinic/model/Vet.java`
  ```java
  // BEFORE
  import javax.persistence.Entity;
  import javax.persistence.FetchType;
  import javax.persistence.JoinColumn;
  import javax.persistence.JoinTable;
  import javax.persistence.ManyToMany;
  import javax.persistence.Table;
  
  // AFTER
  import jakarta.persistence.Entity;
  import jakarta.persistence.FetchType;
  import jakarta.persistence.JoinColumn;
  import jakarta.persistence.JoinTable;
  import jakarta.persistence.ManyToMany;
  import jakarta.persistence.Table;
  ```

- [ ] `monolith/src/main/java/org/springframework/samples/petclinic/model/Visit.java`
  ```java
  // BEFORE
  import javax.persistence.Column;
  import javax.persistence.Entity;
  import javax.persistence.Table;
  
  // AFTER
  import jakarta.persistence.Column;
  import jakarta.persistence.Entity;
  import jakarta.persistence.Table;
  ```

### ✅ Validation Imports (7 occurrences)

#### Files to Update:
- [ ] `monolith/src/main/java/org/springframework/samples/petclinic/model/Person.java`
  ```java
  // BEFORE
  import javax.validation.constraints.NotEmpty;
  
  // AFTER
  import jakarta.validation.constraints.NotEmpty;
  ```

- [ ] `monolith/src/main/java/org/springframework/samples/petclinic/model/Owner.java`
  ```java
  // BEFORE
  import javax.validation.constraints.Digits;
  import javax.validation.constraints.NotEmpty;
  
  // AFTER
  import jakarta.validation.constraints.Digits;
  import jakarta.validation.constraints.NotEmpty;
  ```

- [ ] `monolith/src/main/java/org/springframework/samples/petclinic/model/Visit.java`
  ```java
  // BEFORE
  import javax.validation.constraints.NotEmpty;
  
  // AFTER
  import jakarta.validation.constraints.NotEmpty;
  ```

- [ ] `monolith/src/main/java/org/springframework/samples/petclinic/controller/OwnerController.java`
  ```java
  // BEFORE
  import javax.validation.Valid;
  
  // AFTER
  import jakarta.validation.Valid;
  ```

- [ ] `monolith/src/main/java/org/springframework/samples/petclinic/controller/PetController.java`
  ```java
  // BEFORE
  import javax.validation.Valid;
  
  // AFTER
  import jakarta.validation.Valid;
  ```

- [ ] `monolith/src/main/java/org/springframework/samples/petclinic/controller/VisitController.java`
  ```java
  // BEFORE
  import javax.validation.Valid;
  
  // AFTER
  import jakarta.validation.Valid;
  ```

- [ ] `monolith/src/test/java/org/springframework/samples/petclinic/model/ValidatorTests.java`
  ```java
  // BEFORE
  import javax.validation.ConstraintViolation;
  import javax.validation.Validator;
  
  // AFTER
  import jakarta.validation.ConstraintViolation;
  import jakarta.validation.Validator;
  ```

## Dependency Updates

### ✅ Maven Dependencies
- [ ] Update Spring Boot parent version in `pom.xml`
- [ ] Update Java version to 17 in `pom.xml`
- [ ] Update Maven wrapper in `.mvn/wrapper/maven-wrapper.properties`
- [ ] Update all WebJars versions
- [ ] Update WireMock to Jakarta-compatible version
- [ ] Update ActiveMQ to Jakarta-compatible version

## Build Configuration

### ✅ Maven Plugins
- [ ] Update WRO4J plugin or find Jakarta-compatible alternative
- [ ] Update Maven Surefire plugin for Java 17
- [ ] Update Maven Compiler plugin for Java 17

## Testing and Validation

### ✅ Compilation Tests
- [ ] Run `mvn clean compile` to check for compilation errors
- [ ] Fix any remaining javax references
- [ ] Verify all imports resolve correctly

### ✅ Unit Tests
- [ ] Run `mvn test` to execute all unit tests
- [ ] Fix any test failures related to Jakarta migration
- [ ] Update test dependencies if needed

### ✅ Integration Tests
- [ ] Run full application startup test
- [ ] Test JPA entity operations
- [ ] Test validation constraints
- [ ] Test web controllers with validation

### ✅ Manual Testing
- [ ] Start application successfully
- [ ] Test CRUD operations on all entities
- [ ] Test form validation in web UI
- [ ] Test ActiveMQ integration
- [ ] Test management endpoints

## Post-Migration Verification

### ✅ Code Quality
- [ ] Run static analysis tools
- [ ] Check for any remaining javax references
- [ ] Verify no deprecated API usage
- [ ] Update documentation

### ✅ Performance Testing
- [ ] Compare startup time with previous version
- [ ] Run load tests to ensure no performance regression
- [ ] Monitor memory usage

## Rollback Plan

### ✅ Rollback Preparation
- [ ] Document current working state
- [ ] Create rollback scripts
- [ ] Test rollback procedure
- [ ] Prepare communication plan

## Automation Scripts

### Find and Replace Script
```bash
# Find all javax imports
find . -name "*.java" -exec grep -l "import javax\." {} \;

# Replace javax.persistence with jakarta.persistence
find . -name "*.java" -exec sed -i 's/import javax\.persistence\./import jakarta.persistence./g' {} \;

# Replace javax.validation with jakarta.validation  
find . -name "*.java" -exec sed -i 's/import javax\.validation\./import jakarta.validation./g' {} \;
```

### Verification Script
```bash
# Check for remaining javax imports
find . -name "*.java" -exec grep -n "import javax\." {} \; | grep -v target/

# Compile and test
mvn clean compile test
```

---
**Migration Status**: ⏳ Pending
**Estimated Time**: 1-2 days for code changes + 2-3 days for testing
**Risk Level**: Medium (well-documented migration path)