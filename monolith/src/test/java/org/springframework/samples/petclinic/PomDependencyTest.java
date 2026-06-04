package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test to verify that spring-boot-starter-security dependency is added to pom.xml
 */
class PomDependencyTest {

    @Test
    void pomXmlShouldContainSpringSecurityDependency() throws IOException {
        // Read the pom.xml file
        String pomContent = Files.readString(Paths.get("pom.xml"));
        
        // Verify spring-boot-starter-security dependency is present
        assertThat(pomContent)
            .as("pom.xml should contain spring-boot-starter-security dependency")
            .contains("spring-boot-starter-security");
    }

    @Test
    void pomXmlShouldContainSpringSecurityTestDependency() throws IOException {
        // Read the pom.xml file
        String pomContent = Files.readString(Paths.get("pom.xml"));
        
        // Verify spring-security-test dependency is present for testing
        assertThat(pomContent)
            .as("pom.xml should contain spring-security-test dependency for testing")
            .contains("spring-security-test");
    }
}