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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test to verify that javax packages have been migrated to jakarta packages
 * as required by Spring Boot 3.x migration.
 */
class JakartaMigrationTest {

    @Test
    void ownerShouldUseJakartaImports() throws IOException {
        Path ownerPath = Paths.get("src/main/java/org/springframework/samples/petclinic/model/Owner.java");
        String content = Files.readString(ownerPath);
        
        // Should not contain javax imports
        assertThat(content).doesNotContain("import javax.persistence");
        assertThat(content).doesNotContain("import javax.validation");
        
        // Should contain jakarta imports
        assertThat(content).contains("import jakarta.persistence");
        assertThat(content).contains("import jakarta.validation");
    }

    @Test
    void petShouldUseJakartaImports() throws IOException {
        Path petPath = Paths.get("src/main/java/org/springframework/samples/petclinic/model/Pet.java");
        String content = Files.readString(petPath);
        
        // Should not contain javax imports
        assertThat(content).doesNotContain("import javax.persistence");
        
        // Should contain jakarta imports
        assertThat(content).contains("import jakarta.persistence");
    }

    @Test
    void visitShouldUseJakartaImports() throws IOException {
        Path visitPath = Paths.get("src/main/java/org/springframework/samples/petclinic/model/Visit.java");
        String content = Files.readString(visitPath);
        
        // Should not contain javax imports
        assertThat(content).doesNotContain("import javax.persistence");
        assertThat(content).doesNotContain("import javax.validation");
        
        // Should contain jakarta imports
        assertThat(content).contains("import jakarta.persistence");
        assertThat(content).contains("import jakarta.validation");
    }

    @Test
    void vetShouldUseJakartaImports() throws IOException {
        Path vetPath = Paths.get("src/main/java/org/springframework/samples/petclinic/model/Vet.java");
        String content = Files.readString(vetPath);
        
        // Should not contain javax imports
        assertThat(content).doesNotContain("import javax.persistence");
        
        // Should contain jakarta imports
        assertThat(content).contains("import jakarta.persistence");
    }

    @Test
    void specialtyShouldUseJakartaImports() throws IOException {
        Path specialtyPath = Paths.get("src/main/java/org/springframework/samples/petclinic/model/Specialty.java");
        String content = Files.readString(specialtyPath);
        
        // Should not contain javax imports
        assertThat(content).doesNotContain("import javax.persistence");
        
        // Should contain jakarta imports
        assertThat(content).contains("import jakarta.persistence");
    }
}