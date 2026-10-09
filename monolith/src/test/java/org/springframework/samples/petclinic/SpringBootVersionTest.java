package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringBootVersion;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies that the Spring Boot version is 2.7.x (not 2.2.x).
 * This test was written as part of the CVE-2022-22965 (Spring4Shell) and
 * CVE-2020-1938 (Ghostcat) remediation upgrade from 2.2.1.RELEASE to 2.7.x.
 */
class SpringBootVersionTest {

    @Test
    void springBootVersionShouldBe27x() {
        String version = SpringBootVersion.getVersion();
        assertThat(version)
            .as("Spring Boot version should be 2.7.x to remediate Spring4Shell (CVE-2022-22965) and Ghostcat (CVE-2020-1938)")
            .isNotNull()
            .startsWith("2.7.");
    }
}
