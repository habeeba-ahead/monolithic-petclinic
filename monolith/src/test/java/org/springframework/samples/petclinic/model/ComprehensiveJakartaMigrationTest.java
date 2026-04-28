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
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Comprehensive test to verify that javax packages have been completely migrated 
 * to jakarta packages as required by Spring Boot 3.x migration.
 */
class ComprehensiveJakartaMigrationTest {

    @Test
    void noJavaxPersistenceImportsRemainInMainCode() throws IOException {
        List<Path> mainJavaFiles = findMainJavaFiles();
        
        for (Path javaFile : mainJavaFiles) {
            String content = Files.readString(javaFile);
            assertThat(content)
                .as("File %s should not contain javax.persistence imports", javaFile)
                .doesNotContain("import javax.persistence");
        }
    }

    @Test
    void noJavaxValidationImportsRemainInMainCode() throws IOException {
        List<Path> mainJavaFiles = findMainJavaFiles();
        
        for (Path javaFile : mainJavaFiles) {
            String content = Files.readString(javaFile);
            assertThat(content)
                .as("File %s should not contain javax.validation imports", javaFile)
                .doesNotContain("import javax.validation");
        }
    }

    @Test
    void noJavaxServletImportsRemainInMainCode() throws IOException {
        List<Path> mainJavaFiles = findMainJavaFiles();
        
        for (Path javaFile : mainJavaFiles) {
            String content = Files.readString(javaFile);
            assertThat(content)
                .as("File %s should not contain javax.servlet imports", javaFile)
                .doesNotContain("import javax.servlet");
        }
    }

    @Test
    void jakartaPersistenceImportsPresent() throws IOException {
        // Files that should have jakarta.persistence imports
        String[] persistenceFiles = {
            "src/main/java/org/springframework/samples/petclinic/model/BaseEntity.java",
            "src/main/java/org/springframework/samples/petclinic/model/Owner.java",
            "src/main/java/org/springframework/samples/petclinic/model/Pet.java",
            "src/main/java/org/springframework/samples/petclinic/model/Vet.java",
            "src/main/java/org/springframework/samples/petclinic/model/Visit.java",
            "src/main/java/org/springframework/samples/petclinic/model/Specialty.java",
            "src/main/java/org/springframework/samples/petclinic/model/PetType.java",
            "src/main/java/org/springframework/samples/petclinic/model/NamedEntity.java",
            "src/main/java/org/springframework/samples/petclinic/model/Person.java"
        };
        
        for (String filePath : persistenceFiles) {
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                String content = Files.readString(path);
                assertThat(content)
                    .as("File %s should contain jakarta.persistence imports", filePath)
                    .contains("import jakarta.persistence");
            }
        }
    }

    @Test
    void jakartaValidationImportsPresent() throws IOException {
        // Files that should have jakarta.validation imports
        String[] validationFiles = {
            "src/main/java/org/springframework/samples/petclinic/model/Owner.java",
            "src/main/java/org/springframework/samples/petclinic/model/Visit.java",
            "src/main/java/org/springframework/samples/petclinic/model/Person.java",
            "src/main/java/org/springframework/samples/petclinic/controller/OwnerController.java",
            "src/main/java/org/springframework/samples/petclinic/controller/PetController.java",
            "src/main/java/org/springframework/samples/petclinic/controller/VisitController.java"
        };
        
        for (String filePath : validationFiles) {
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                String content = Files.readString(path);
                assertThat(content)
                    .as("File %s should contain jakarta.validation imports", filePath)
                    .contains("import jakarta.validation");
            }
        }
    }

    @Test
    void applicationCompilesSuccessfully() throws IOException, InterruptedException {
        // This test verifies that the application compiles without javax import errors
        ProcessBuilder pb = new ProcessBuilder("mvn", "compile", "-q");
        pb.directory(Paths.get(".").toFile());
        Process process = pb.start();
        int exitCode = process.waitFor();
        
        assertThat(exitCode)
            .as("Application should compile successfully without javax import errors")
            .isEqualTo(0);
    }

    private List<Path> findMainJavaFiles() throws IOException {
        try (Stream<Path> paths = Files.walk(Paths.get("src/main"))) {
            return paths
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".java"))
                .toList();
        }
    }
}