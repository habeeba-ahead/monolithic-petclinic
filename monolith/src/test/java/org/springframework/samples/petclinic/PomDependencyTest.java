package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public class PomDependencyTest {

    @Test
    public void pomContainsSpringSecurityDependency() throws Exception {
        // Parse the pom.xml file
        File pomFile = new File("pom.xml");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(pomFile);

        // Look for spring-boot-starter-security dependency
        NodeList dependencies = document.getElementsByTagName("dependency");
        boolean foundSecurityDependency = false;

        for (int i = 0; i < dependencies.getLength(); i++) {
            NodeList children = dependencies.item(i).getChildNodes();
            String groupId = null;
            String artifactId = null;

            for (int j = 0; j < children.getLength(); j++) {
                if ("groupId".equals(children.item(j).getNodeName())) {
                    groupId = children.item(j).getTextContent();
                }
                if ("artifactId".equals(children.item(j).getNodeName())) {
                    artifactId = children.item(j).getTextContent();
                }
            }

            if ("org.springframework.boot".equals(groupId) && 
                "spring-boot-starter-security".equals(artifactId)) {
                foundSecurityDependency = true;
                break;
            }
        }

        assertThat(foundSecurityDependency)
            .as("spring-boot-starter-security dependency should be present in pom.xml")
            .isTrue();
    }
}