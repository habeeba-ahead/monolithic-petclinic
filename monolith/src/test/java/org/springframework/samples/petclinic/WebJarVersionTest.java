package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies that the root pom.xml WebJar versions have been upgraded to
 * eliminate known XSS and prototype-pollution CVEs.
 */
class WebJarVersionTest {

    private static final Path ROOT_POM = Paths.get(
            System.getProperty("user.dir")).getParent().resolve("pom.xml");

    @Test
    void bootstrapVersionShouldBe5x() throws Exception {
        String version = readPomProperty("webjars-bootstrap.version");
        assertThat(version)
                .as("webjars-bootstrap.version should be 5.x")
                .startsWith("5.");
    }

    @Test
    void jqueryVersionShouldBe37x() throws Exception {
        String version = readPomProperty("webjars-jquery.version");
        assertThat(version)
                .as("webjars-jquery.version should be 3.7.x")
                .startsWith("3.7.");
    }

    @Test
    void jqueryUiVersionShouldBe113xOrLater() throws Exception {
        String version = readPomProperty("webjars-jquery-ui.version");
        assertThat(version)
                .as("webjars-jquery-ui.version should be 1.13.x or later")
                .satisfies(v -> {
                    String[] parts = v.split("\\.");
                    int major = Integer.parseInt(parts[0]);
                    int minor = Integer.parseInt(parts[1]);
                    assertThat(major > 1 || (major == 1 && minor >= 13))
                            .as("Expected version >= 1.13 but got " + v)
                            .isTrue();
                });
    }

    private String readPomProperty(String propertyName) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(ROOT_POM.toFile());

        XPath xpath = XPathFactory.newInstance().newXPath();
        // pom.xml uses default namespace; use local-name() to avoid NS issues
        String expression = "//*[local-name()='properties']/*[local-name()='" + propertyName + "']";
        NodeList nodes = (NodeList) xpath.evaluate(expression, doc, XPathConstants.NODESET);
        assertThat(nodes.getLength())
                .as("Property '" + propertyName + "' not found in pom.xml")
                .isGreaterThan(0);
        return nodes.item(0).getTextContent().trim();
    }
}
