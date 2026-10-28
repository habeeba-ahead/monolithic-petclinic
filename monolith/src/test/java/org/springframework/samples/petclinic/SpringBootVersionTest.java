package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringBootVersion;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TDD test verifying that the Spring Boot version has been upgraded to 2.7.x.
 * This test will FAIL on Spring Boot 2.2.x and PASS on Spring Boot 2.7.x.
 */
class SpringBootVersionTest {

    @Test
    void springBootVersionShouldBe27x() {
        String version = SpringBootVersion.getVersion();
        assertThat(version)
            .as("Spring Boot version should be 2.7.x (not 2.2.x)")
            .startsWith("2.7.");
    }
}
