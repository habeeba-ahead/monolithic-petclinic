package org.springframework.samples.petclinic.config;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TDD tests verifying that all environment-specific values in application.properties
 * files are externalized via Spring Boot property placeholders backed by env vars.
 *
 * Acceptance criteria:
 *  - grep -r 'localhost' monolith/src/main/resources/ returns only placeholder defaults
 *  - grep -r 'localhost' activemq/src/main/resources/ returns only placeholder defaults
 *  - docs/env-vars.md exists and documents every env variable
 */
class ExternalizedConfigTest {

    /**
     * Resolve the repository root by walking up from the compiled test-classes directory.
     * Compiled class is at: <repo>/monolith/target/test-classes/org/.../config/ExternalizedConfigTest.class
     * So we go up: config -> petclinic -> samples -> springframework -> org -> test-classes -> target -> monolith -> <repo>
     */
    private static Path repoRoot() throws URISyntaxException {
        Path classFile = Paths.get(
                ExternalizedConfigTest.class.getProtectionDomain().getCodeSource().getLocation().toURI()
        );
        // classFile is the test-classes directory (or a jar); walk up to repo root
        // target/test-classes -> target -> monolith -> repo
        return classFile.getParent().getParent().getParent();
    }

    private static Path monolithProps() throws URISyntaxException {
        return repoRoot().resolve("monolith/src/main/resources/application.properties");
    }

    private static Path activemqProps() throws URISyntaxException {
        return repoRoot().resolve("activemq/src/main/resources/application.properties");
    }

    private static Path envVarsDoc() throws URISyntaxException {
        return repoRoot().resolve("docs/env-vars.md");
    }

    @Test
    void monolithPropertiesShouldNotContainBareLocalhost() throws IOException, URISyntaxException {
        List<String> lines = Files.readAllLines(monolithProps());
        for (String line : lines) {
            String trimmed = line.trim();
            // Skip comment lines
            if (trimmed.startsWith("#")) continue;
            if (trimmed.contains("localhost")) {
                // Must be inside a placeholder default: ${VAR:...localhost...}
                assertThat(trimmed)
                        .as("Line should use placeholder syntax for localhost: " + trimmed)
                        .containsPattern("\\$\\{[^}]+:.*localhost.*\\}");
            }
        }
    }

    @Test
    void activemqPropertiesShouldNotContainBareLocalhost() throws IOException, URISyntaxException {
        List<String> lines = Files.readAllLines(activemqProps());
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.startsWith("#")) continue;
            if (trimmed.contains("localhost")) {
                assertThat(trimmed)
                        .as("Line should use placeholder syntax for localhost: " + trimmed)
                        .containsPattern("\\$\\{[^}]+:.*localhost.*\\}");
            }
        }
    }

    @Test
    void monolithPropertiesShouldExternalizeActivemqBrokerUrl() throws IOException, URISyntaxException {
        String content = Files.readString(monolithProps());
        assertThat(content)
                .as("spring.activemq.broker-url should use ACTIVEMQ_BROKER_URL env var placeholder")
                .contains("spring.activemq.broker-url=${ACTIVEMQ_BROKER_URL:");
    }

    @Test
    void monolithPropertiesShouldExternalizeServerPort() throws IOException, URISyntaxException {
        String content = Files.readString(monolithProps());
        assertThat(content)
                .as("server.port should use SERVER_PORT env var placeholder")
                .contains("server.port=${SERVER_PORT:");
    }

    @Test
    void monolithPropertiesShouldExternalizeDatasourceUrl() throws IOException, URISyntaxException {
        String content = Files.readString(monolithProps());
        assertThat(content)
                .as("spring.datasource.url should use DB_URL env var placeholder")
                .contains("spring.datasource.url=${DB_URL:");
    }

    @Test
    void monolithPropertiesShouldExternalizeDatasourceUsername() throws IOException, URISyntaxException {
        String content = Files.readString(monolithProps());
        assertThat(content)
                .as("spring.datasource.username should use DB_USERNAME env var placeholder")
                .contains("spring.datasource.username=${DB_USERNAME:");
    }

    @Test
    void monolithPropertiesShouldExternalizeDatasourcePassword() throws IOException, URISyntaxException {
        String content = Files.readString(monolithProps());
        assertThat(content)
                .as("spring.datasource.password should use DB_PASSWORD env var placeholder")
                .contains("spring.datasource.password=${DB_PASSWORD:");
    }

    @Test
    void activemqPropertiesShouldExternalizeBrokerUrl() throws IOException, URISyntaxException {
        String content = Files.readString(activemqProps());
        assertThat(content)
                .as("activemq.broker-url should use ACTIVEMQ_BROKER_URL env var placeholder")
                .contains("activemq.broker-url=${ACTIVEMQ_BROKER_URL:");
    }

    @Test
    void activemqPropertiesShouldExternalizeServerPort() throws IOException, URISyntaxException {
        String content = Files.readString(activemqProps());
        assertThat(content)
                .as("server.port should use SERVER_PORT env var placeholder")
                .contains("server.port=${SERVER_PORT:");
    }

    @Test
    void envVarsDocumentationFileShouldExist() throws URISyntaxException {
        assertThat(envVarsDoc())
                .as("docs/env-vars.md should exist")
                .exists();
    }

    @Test
    void envVarsDocShouldDocumentAllRequiredVariables() throws IOException, URISyntaxException {
        String content = Files.readString(envVarsDoc());
        assertThat(content).as("docs/env-vars.md should document DB_URL").contains("DB_URL");
        assertThat(content).as("docs/env-vars.md should document DB_USERNAME").contains("DB_USERNAME");
        assertThat(content).as("docs/env-vars.md should document DB_PASSWORD").contains("DB_PASSWORD");
        assertThat(content).as("docs/env-vars.md should document ACTIVEMQ_BROKER_URL").contains("ACTIVEMQ_BROKER_URL");
        assertThat(content).as("docs/env-vars.md should document SERVER_PORT").contains("SERVER_PORT");
    }

    @Test
    void envVarsDocShouldIncludeDefaultValues() throws IOException, URISyntaxException {
        String content = Files.readString(envVarsDoc());
        // Check that default values are documented
        assertThat(content).as("docs/env-vars.md should document HSQLDB default").contains("hsqldb");
        assertThat(content).as("docs/env-vars.md should document default port 8080").contains("8080");
        assertThat(content).as("docs/env-vars.md should document default ActiveMQ port").contains("61616");
    }
}
