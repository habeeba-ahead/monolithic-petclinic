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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies that all JPA entity classes in the model package use
 * jakarta.persistence.* imports instead of the legacy javax.persistence.* imports.
 *
 * Spring Boot 3.x / Hibernate 6.x requires the Jakarta EE 9 namespace.
 */
class JakartaPersistenceImportTest {

    private static final String MODEL_DIR =
            "src/main/java/org/springframework/samples/petclinic/model";

    private static final List<String> ENTITY_FILES = List.of(
            "BaseEntity.java",
            "Owner.java",
            "Pet.java",
            "PetType.java",
            "Visit.java",
            "Vet.java",
            "Specialty.java",
            "NamedEntity.java",
            "Person.java"
    );

    @Test
    void noEntityFileShouldContainJavaxPersistenceImport() throws IOException {
        for (String fileName : ENTITY_FILES) {
            Path filePath = Paths.get(MODEL_DIR, fileName);
            String content = Files.readString(filePath);
            assertThat(content)
                    .as("File %s must not contain 'javax.persistence' imports", fileName)
                    .doesNotContain("import javax.persistence.");
        }
    }

    @Test
    void everyEntityFileThatHadPersistenceAnnotationsShouldUseJakartaImport() throws IOException {
        // These files are known to have JPA annotations and must use jakarta.persistence
        List<String> filesWithJpaAnnotations = List.of(
                "BaseEntity.java",
                "Owner.java",
                "Pet.java",
                "PetType.java",
                "Visit.java",
                "Vet.java",
                "Specialty.java",
                "NamedEntity.java",
                "Person.java"
        );

        for (String fileName : filesWithJpaAnnotations) {
            Path filePath = Paths.get(MODEL_DIR, fileName);
            String content = Files.readString(filePath);
            assertThat(content)
                    .as("File %s must contain at least one 'jakarta.persistence' import", fileName)
                    .contains("import jakarta.persistence.");
        }
    }
}
