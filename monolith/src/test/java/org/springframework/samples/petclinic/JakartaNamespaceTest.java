package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies that no legacy EE namespace imports remain in the source tree.
 * Spring Boot 3.x requires the Jakarta EE 9+ namespace (jakarta.*).
 */
class JakartaNamespaceTest {

    // The forbidden import prefix - kept as a concatenation so this file itself
    // does not appear in a grep for the literal string.
    private static final String JAVAX_PERSISTENCE = "import " + "javax" + ".persistence.";
    private static final String JAVAX_VALIDATION  = "import " + "javax" + ".validation.";
    private static final String JAVAX_TRANSACTION = "import " + "javax" + ".transaction.";

    @Test
    void mainSourcesShouldNotContainLegacyJavaxImports() throws IOException {
        List<String> violations = findForbiddenImports(Paths.get("src/main/java"));
        assertThat(violations)
            .as("Legacy javax EE imports found in src/main/java – migrate to jakarta.* for Spring Boot 3.x")
            .isEmpty();
    }

    @Test
    void testSourcesShouldNotContainLegacyJavaxImports() throws IOException {
        List<String> violations = findForbiddenImports(Paths.get("src/test/java"));
        assertThat(violations)
            .as("Legacy javax EE imports found in src/test/java – migrate to jakarta.* for Spring Boot 3.x")
            .isEmpty();
    }

    private List<String> findForbiddenImports(Path root) throws IOException {
        List<String> violations = new ArrayList<>();
        if (!root.toFile().exists()) {
            return violations;
        }
        try (Stream<Path> paths = Files.walk(root)) {
            paths.filter(p -> p.toString().endsWith(".java"))
                .forEach(file -> {
                    try {
                        List<String> lines = Files.readAllLines(file);
                        for (int i = 0; i < lines.size(); i++) {
                            String line = lines.get(i);
                            if (line.contains(JAVAX_PERSISTENCE)
                                || line.contains(JAVAX_VALIDATION)
                                || line.contains(JAVAX_TRANSACTION)) {
                                violations.add(file + ":" + (i + 1) + ": " + line.trim());
                            }
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
        }
        return violations;
    }
}
