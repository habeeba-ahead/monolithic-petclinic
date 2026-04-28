package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test to verify Spring Boot version upgrade to 3.x
 */
@SpringBootTest
class SpringBootVersionTest {

    @Test
    void shouldUseSpringBoot3x() {
        String version = SpringBootVersion.getVersion();
        assertThat(version).isNotNull();
        assertThat(version).startsWith("3.");
    }
}