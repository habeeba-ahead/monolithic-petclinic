package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies that DevTools is disabled in production configuration.
 * Defense-in-depth: even if devtools jar is on classpath, these properties
 * ensure restart and livereload are explicitly disabled.
 */
@SpringBootTest
class DevToolsConfigTest {

    @Autowired
    private Environment environment;

    @Test
    void devtoolsRestartShouldBeDisabled() {
        String value = environment.getProperty("spring.devtools.restart.enabled");
        assertThat(value)
            .as("spring.devtools.restart.enabled must be set to false in application.properties")
            .isEqualTo("false");
    }

    @Test
    void devtoolsLivereloadShouldBeDisabled() {
        String value = environment.getProperty("spring.devtools.livereload.enabled");
        assertThat(value)
            .as("spring.devtools.livereload.enabled must be set to false in application.properties")
            .isEqualTo("false");
    }
}
