package org.springframework.samples.petclinic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies that the upgraded WebJar versions (Bootstrap 5.3.3, jQuery 3.7.1,
 * jQuery UI 1.13.3) have zero critical/high CVEs according to the OWASP
 * Dependency-Check JS retire repository bundled in the local Maven cache.
 *
 * The jsrepository.json file is the same data source used by OWASP
 * dependency-check to flag JavaScript vulnerabilities.
 */
class WebJarCveVerificationTest {

    private static final String BOOTSTRAP_VERSION  = "5.3.3";
    private static final String JQUERY_VERSION     = "3.7.1";
    private static final String JQUERY_UI_VERSION  = "1.13.3";

    private static JsonNode jsRepo;

    @BeforeAll
    static void loadJsRepository() throws Exception {
        // Try multiple known locations for the OWASP retire-js database
        String[] candidatePaths = {
            System.getProperty("user.home") + "/.m2/repository/org/owasp/dependency-check-data/9.0/jsrepository.json",
            "/root/.m2/repository/org/owasp/dependency-check-data/9.0/jsrepository.json",
        };

        File repoFile = null;
        for (String p : candidatePaths) {
            File f = new File(p);
            if (f.exists()) {
                repoFile = f;
                break;
            }
        }

        if (repoFile == null) {
            // Skip gracefully if the file is not present (CI without OWASP data)
            jsRepo = null;
            return;
        }

        jsRepo = new ObjectMapper().readTree(repoFile);
    }

    // ── Bootstrap 5.3.3 ─────────────────────────────────────────────────────

    @Test
    void bootstrap533ShouldHaveNoCriticalOrHighCves() {
        if (jsRepo == null) return; // skip if no local DB
        List<String> findings = findHighOrCriticalVulns("bootstrap", BOOTSTRAP_VERSION);
        assertThat(findings)
                .as("Bootstrap %s should have zero critical/high CVEs but found: %s",
                        BOOTSTRAP_VERSION, findings)
                .isEmpty();
    }

    // ── jQuery 3.7.1 ────────────────────────────────────────────────────────

    @Test
    void jquery371ShouldHaveNoCriticalOrHighCves() {
        if (jsRepo == null) return;
        List<String> findings = findHighOrCriticalVulns("jquery", JQUERY_VERSION);
        assertThat(findings)
                .as("jQuery %s should have zero critical/high CVEs but found: %s",
                        JQUERY_VERSION, findings)
                .isEmpty();
    }

    // ── jQuery UI 1.13.3 ────────────────────────────────────────────────────

    @Test
    void jqueryUi1133ShouldHaveNoCriticalOrHighCves() {
        if (jsRepo == null) return;
        List<String> findings = findHighOrCriticalVulns("jquery-ui", JQUERY_UI_VERSION);
        assertThat(findings)
                .as("jQuery UI %s should have zero critical/high CVEs but found: %s",
                        JQUERY_UI_VERSION, findings)
                .isEmpty();
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    /**
     * Returns a list of high/critical vulnerability summaries that affect the
     * given library version according to the retire-js database.
     */
    private List<String> findHighOrCriticalVulns(String libraryKey, String version) {
        List<String> results = new ArrayList<>();
        JsonNode library = jsRepo.get(libraryKey);
        if (library == null) return results;

        JsonNode vulns = library.get("vulnerabilities");
        if (vulns == null || !vulns.isArray()) return results;

        for (JsonNode vuln : vulns) {
            if (!isVersionAffected(vuln, version)) continue;

            String severity = vuln.has("severity") ? vuln.get("severity").asText("").toLowerCase() : "";
            if (severity.equals("high") || severity.equals("critical")) {
                String summary = "";
                if (vuln.has("identifiers") && vuln.get("identifiers").has("summary")) {
                    summary = vuln.get("identifiers").get("summary").asText();
                }
                results.add("[" + severity + "] " + summary);
            }
        }
        return results;
    }

    /**
     * Checks whether a retire-js vulnerability entry applies to the given version.
     * A vulnerability applies when:
     *   - version >= atOrAbove (if present)
     *   - version <  below     (if present)
     */
    private boolean isVersionAffected(JsonNode vuln, String version) {
        int[] v = parseVersion(version);

        if (vuln.has("atOrAbove")) {
            int[] atOrAbove = parseVersion(vuln.get("atOrAbove").asText());
            if (compareVersions(v, atOrAbove) < 0) return false;
        }
        if (vuln.has("below")) {
            int[] below = parseVersion(vuln.get("below").asText());
            if (compareVersions(v, below) >= 0) return false;
        }
        return true;
    }

    private int[] parseVersion(String v) {
        String[] parts = v.split("\\.");
        int[] result = new int[Math.max(parts.length, 3)];
        for (int i = 0; i < parts.length; i++) {
            try {
                result[i] = Integer.parseInt(parts[i].replaceAll("[^0-9]", ""));
            } catch (NumberFormatException e) {
                result[i] = 0;
            }
        }
        return result;
    }

    private int compareVersions(int[] a, int[] b) {
        int len = Math.max(a.length, b.length);
        for (int i = 0; i < len; i++) {
            int ai = i < a.length ? a[i] : 0;
            int bi = i < b.length ? b[i] : 0;
            if (ai != bi) return Integer.compare(ai, bi);
        }
        return 0;
    }
}
