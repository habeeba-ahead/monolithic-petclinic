package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies that data.sql contains no realistic PII (names, addresses, phone numbers).
 * Acceptance criteria: grep -E '[0-9]{10}|Franklin|Coleman|Davis|Rodriquez|McTavish|Hernadez|Leary|Schroeder|Wexler|Tel'
 * returns no matches.
 */
class DataSqlPiiTest {

    private static final Pattern PII_PATTERN = Pattern.compile(
        "[0-9]{10}|Franklin|Coleman|Davis|Rodriquez|McTavish|Hernadez|Leary|Schroeder|Wexler|Tel"
    );

    private static final Pattern OWNER_NAME_PATTERN = Pattern.compile(
        "INSERT INTO owners VALUES \\(\\d+, 'Test', 'Owner \\d+',"
    );

    private static final Pattern OWNER_ADDRESS_PATTERN = Pattern.compile(
        "'123 Test St'"
    );

    // Phone numbers are all-digit to satisfy @Digits(integer=10) constraint.
    // Pattern: 555010N where N is 0-9 (7 digits, clearly synthetic, no 10-digit sequence).
    private static final Pattern OWNER_PHONE_PATTERN = Pattern.compile(
        "'555010\\d'"
    );

    @Test
    void dataSqlShouldContainNoPii() throws IOException {
        Path dataFile = findDataSql();
        List<String> lines = Files.readAllLines(dataFile);

        List<String> piiLines = lines.stream()
            .filter(line -> PII_PATTERN.matcher(line).find())
            .collect(Collectors.toList());

        assertThat(piiLines)
            .as("data.sql should contain no PII matching pattern; offending lines: %s", piiLines)
            .isEmpty();
    }

    @Test
    void ownerRowsShouldUseSyntheticNames() throws IOException {
        Path dataFile = findDataSql();
        List<String> ownerLines = Files.readAllLines(dataFile).stream()
            .filter(line -> line.startsWith("INSERT INTO owners VALUES"))
            .collect(Collectors.toList());

        assertThat(ownerLines).isNotEmpty();

        for (String line : ownerLines) {
            assertThat(OWNER_NAME_PATTERN.matcher(line).find())
                .as("Owner row should match 'Test Owner N' pattern: %s", line)
                .isTrue();
            assertThat(OWNER_ADDRESS_PATTERN.matcher(line).find())
                .as("Owner row should contain '123 Test St' address: %s", line)
                .isTrue();
            assertThat(OWNER_PHONE_PATTERN.matcher(line).find())
                .as("Owner row should contain '555010N' phone (synthetic, all-digit): %s", line)
                .isTrue();
        }
    }

    private Path findDataSql() {
        // Try relative to working directory (project root)
        Path[] candidates = {
            Paths.get("monolith/src/main/resources/db/data.sql"),
            Paths.get("src/main/resources/db/data.sql"),
            Paths.get("../monolith/src/main/resources/db/data.sql"),
        };
        for (Path candidate : candidates) {
            if (Files.exists(candidate)) {
                return candidate;
            }
        }
        // Try from class location
        throw new IllegalStateException("Cannot find data.sql; tried: " + java.util.Arrays.toString(candidates));
    }
}
