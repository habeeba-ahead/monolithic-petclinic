package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Acceptance-criteria verification test for the Bootstrap/jQuery/jQuery-UI upgrade.
 *
 * Criteria verified:
 *  1. webjars-bootstrap.version is 5.x
 *  2. webjars-jquery.version is 3.7.x (not 2.x)
 *  3. webjars-jquery-ui.version is 1.13.x or later
 */
class WebJarDependencyVersionVerificationTest {

    private static final Path ROOT_POM = Paths.get(
            System.getProperty("user.dir")).getParent().resolve("pom.xml");

    // ── Criterion 1: Bootstrap 5.x ──────────────────────────────────────────

    @Test
    void bootstrapVersionShouldBe5x() throws Exception {
        String version = readPomProperty("webjars-bootstrap.version");
        assertThat(version)
                .as("webjars-bootstrap.version must start with '5.' (Bootstrap 5.x)")
                .startsWith("5.");
    }

    @Test
    void bootstrapVersionShouldNotBe3x() throws Exception {
        String version = readPomProperty("webjars-bootstrap.version");
        assertThat(version)
                .as("webjars-bootstrap.version must NOT be Bootstrap 3.x")
                .doesNotStartWith("3.");
    }

    @Test
    void bootstrapVersionShouldNotBe4x() throws Exception {
        String version = readPomProperty("webjars-bootstrap.version");
        assertThat(version)
                .as("webjars-bootstrap.version must NOT be Bootstrap 4.x")
                .doesNotStartWith("4.");
    }

    // ── Criterion 2: jQuery 3.7.x ───────────────────────────────────────────

    @Test
    void jqueryVersionShouldBe37x() throws Exception {
        String version = readPomProperty("webjars-jquery.version");
        assertThat(version)
                .as("webjars-jquery.version must start with '3.7.' (jQuery 3.7.x)")
                .startsWith("3.7.");
    }

    @Test
    void jqueryVersionShouldNotBe2x() throws Exception {
        String version = readPomProperty("webjars-jquery.version");
        assertThat(version)
                .as("webjars-jquery.version must NOT be jQuery 2.x")
                .doesNotStartWith("2.");
    }

    @Test
    void jqueryVersionShouldNotBe1x() throws Exception {
        String version = readPomProperty("webjars-jquery.version");
        assertThat(version)
                .as("webjars-jquery.version must NOT be jQuery 1.x")
                .doesNotStartWith("1.");
    }

    // ── Criterion 3: jQuery UI 1.13.x or later ──────────────────────────────

    @Test
    void jqueryUiVersionShouldBe113xOrLater() throws Exception {
        String version = readPomProperty("webjars-jquery-ui.version");
        String[] parts = version.split("\\.");
        int major = Integer.parseInt(parts[0]);
        int minor = Integer.parseInt(parts[1]);

        assertThat(major > 1 || (major == 1 && minor >= 13))
                .as("webjars-jquery-ui.version must be >= 1.13.x but was: " + version)
                .isTrue();
    }

    @Test
    void jqueryUiVersionShouldNotBe112xOrEarlier() throws Exception {
        String version = readPomProperty("webjars-jquery-ui.version");
        String[] parts = version.split("\\.");
        int major = Integer.parseInt(parts[0]);
        int minor = Integer.parseInt(parts[1]);

        assertThat(major == 1 && minor < 13)
                .as("webjars-jquery-ui.version must NOT be < 1.13 but was: " + version)
                .isFalse();
    }

    // ── Exact version assertions (belt-and-suspenders) ──────────────────────

    @Test
    void bootstrapVersionShouldBe533() throws Exception {
        String version = readPomProperty("webjars-bootstrap.version");
        assertThat(version)
                .as("webjars-bootstrap.version should be exactly 5.3.3")
                .isEqualTo("5.3.3");
    }

    @Test
    void jqueryVersionShouldBe371() throws Exception {
        String version = readPomProperty("webjars-jquery.version");
        assertThat(version)
                .as("webjars-jquery.version should be exactly 3.7.1")
                .isEqualTo("3.7.1");
    }

    @Test
    void jqueryUiVersionShouldBe1133() throws Exception {
        String version = readPomProperty("webjars-jquery-ui.version");
        assertThat(version)
                .as("webjars-jquery-ui.version should be exactly 1.13.3")
                .isEqualTo("1.13.3");
    }

    // ── Helper ───────────────────────────────────────────────────────────────

    private String readPomProperty(String propertyName) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(ROOT_POM.toFile());

        XPath xpath = XPathFactory.newInstance().newXPath();
        String expression = "//*[local-name()='properties']/*[local-name()='" + propertyName + "']";
        NodeList nodes = (NodeList) xpath.evaluate(expression, doc, XPathConstants.NODESET);

        assertThat(nodes.getLength())
                .as("Property '" + propertyName + "' not found in root pom.xml")
                .isGreaterThan(0);

        return nodes.item(0).getTextContent().trim();
    }
}
