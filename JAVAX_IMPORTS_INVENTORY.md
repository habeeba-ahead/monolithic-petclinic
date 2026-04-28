# Complete javax.* Imports Inventory

## Summary
- **Total Files**: 13 files contain javax.* imports (12 main + 1 test)
- **Total Imports**: 41 javax.* import statements
- **Packages Used**: javax.persistence, javax.validation

## Detailed Inventory

### javax.persistence Package (35 imports)

#### BaseEntity.java
```java
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
```

#### NamedEntity.java
```java
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
```

#### Person.java
```java
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
```

#### Owner.java
```java
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
```

#### Pet.java
```java
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
```

#### PetType.java
```java
import javax.persistence.Entity;
import javax.persistence.Table;
```

#### Specialty.java
```java
import javax.persistence.Entity;
import javax.persistence.Table;
```

#### Vet.java
```java
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
```

#### Visit.java
```java
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
```

### javax.validation Package (6 imports)

#### OwnerController.java
```java
import javax.validation.Valid;
```

#### PetController.java
```java
import javax.validation.Valid;
```

#### VisitController.java
```java
import javax.validation.Valid;
```

#### Owner.java
```java
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
```

#### Person.java
```java
import javax.validation.constraints.NotEmpty;
```

#### Visit.java
```java
import javax.validation.constraints.NotEmpty;
```

#### ValidatorTests.java (Test File)
```java
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
```

## Migration Mapping

### javax.persistence → jakarta.persistence
| Current Import | Target Import |
|----------------|---------------|
| javax.persistence.Entity | jakarta.persistence.Entity |
| javax.persistence.Table | jakarta.persistence.Table |
| javax.persistence.Id | jakarta.persistence.Id |
| javax.persistence.GeneratedValue | jakarta.persistence.GeneratedValue |
| javax.persistence.GenerationType | jakarta.persistence.GenerationType |
| javax.persistence.MappedSuperclass | jakarta.persistence.MappedSuperclass |
| javax.persistence.Column | jakarta.persistence.Column |
| javax.persistence.OneToMany | jakarta.persistence.OneToMany |
| javax.persistence.ManyToOne | jakarta.persistence.ManyToOne |
| javax.persistence.ManyToMany | jakarta.persistence.ManyToMany |
| javax.persistence.JoinColumn | jakarta.persistence.JoinColumn |
| javax.persistence.JoinTable | jakarta.persistence.JoinTable |
| javax.persistence.CascadeType | jakarta.persistence.CascadeType |
| javax.persistence.FetchType | jakarta.persistence.FetchType |
| javax.persistence.Transient | jakarta.persistence.Transient |

### javax.validation → jakarta.validation
| Current Import | Target Import |
|----------------|---------------|
| javax.validation.Valid | jakarta.validation.Valid |
| javax.validation.constraints.NotEmpty | jakarta.validation.constraints.NotEmpty |
| javax.validation.constraints.Digits | jakarta.validation.constraints.Digits |
| javax.validation.ConstraintViolation | jakarta.validation.ConstraintViolation |
| javax.validation.Validator | jakarta.validation.Validator |

## Files Requiring Migration

### Main Source Files (12 files)
1. `monolith/src/main/java/org/springframework/samples/petclinic/model/BaseEntity.java`
2. `monolith/src/main/java/org/springframework/samples/petclinic/model/NamedEntity.java`
3. `monolith/src/main/java/org/springframework/samples/petclinic/model/Person.java`
4. `monolith/src/main/java/org/springframework/samples/petclinic/model/Owner.java`
5. `monolith/src/main/java/org/springframework/samples/petclinic/model/Pet.java`
6. `monolith/src/main/java/org/springframework/samples/petclinic/model/PetType.java`
7. `monolith/src/main/java/org/springframework/samples/petclinic/model/Specialty.java`
8. `monolith/src/main/java/org/springframework/samples/petclinic/model/Vet.java`
9. `monolith/src/main/java/org/springframework/samples/petclinic/model/Visit.java`
10. `monolith/src/main/java/org/springframework/samples/petclinic/controller/OwnerController.java`
11. `monolith/src/main/java/org/springframework/samples/petclinic/controller/PetController.java`
12. `monolith/src/main/java/org/springframework/samples/petclinic/controller/VisitController.java`

### Test Files (1 file)
1. `monolith/src/test/java/org/springframework/samples/petclinic/model/ValidatorTests.java`

## Automated Migration Commands

### Using OpenRewrite
```bash
# Add OpenRewrite plugin to pom.xml
mvn org.openrewrite.maven:rewrite-maven-plugin:run \
  -Drewrite.recipeArtifactCoordinates=org.openrewrite.recipe:rewrite-migrate-java:LATEST \
  -Drewrite.activeRecipes=org.openrewrite.java.migrate.javax.JavaxMigrationToJakarta
```

### Using sed (Manual approach)
```bash
# Replace javax.persistence with jakarta.persistence
find monolith/src -name "*.java" -exec sed -i 's/javax\.persistence/jakarta.persistence/g' {} \;

# Replace javax.validation with jakarta.validation  
find monolith/src -name "*.java" -exec sed -i 's/javax\.validation/jakarta.validation/g' {} \;
```

## Verification Script
```bash
# Count remaining javax imports after migration
find monolith/src -name "*.java" -exec grep -H "import javax\." {} \;
```