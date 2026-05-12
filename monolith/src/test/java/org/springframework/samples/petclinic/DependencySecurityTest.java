package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test to verify that frontend dependencies are updated to secure versions
 * to eliminate known CVEs.
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
public class DependencySecurityTest {

    private static final String POM_PATH = "pom.xml";
    
    @Test
    public void testJQueryVersionIsSecure() throws IOException {
        String pomContent = readPomContent();
        String jqueryVersion = extractPropertyValue(pomContent, "webjars-jquery.version");
        
        assertNotNull(jqueryVersion, "jQuery version should be defined");
        assertTrue(isVersionAtLeast(jqueryVersion, "3.6.0"), 
            "jQuery version should be 3.6.0 or later to eliminate CVE-2019-11358, but was: " + jqueryVersion);
    }
    
    @Test
    public void testJQueryUIVersionIsSecure() throws IOException {
        String pomContent = readPomContent();
        String jqueryUIVersion = extractPropertyValue(pomContent, "webjars-jquery-ui.version");
        
        assertNotNull(jqueryUIVersion, "jQuery UI version should be defined");
        assertTrue(isVersionAtLeast(jqueryUIVersion, "1.13.0"), 
            "jQuery UI version should be 1.13.0 or later, but was: " + jqueryUIVersion);
    }
    
    @Test
    public void testBootstrapVersionIsSecure() throws IOException {
        String pomContent = readPomContent();
        String bootstrapVersion = extractPropertyValue(pomContent, "webjars-bootstrap.version");
        
        assertNotNull(bootstrapVersion, "Bootstrap version should be defined");
        assertTrue(isVersionAtLeast(bootstrapVersion, "5.0.0"), 
            "Bootstrap version should be 5.x or later to eliminate CVE-2018-14040, but was: " + bootstrapVersion);
    }
    
    private String readPomContent() throws IOException {
        // Read the parent pom.xml where properties are defined
        return new String(Files.readAllBytes(Paths.get("../pom.xml")));
    }
    
    private String extractPropertyValue(String pomContent, String propertyName) {
        Pattern pattern = Pattern.compile("<" + propertyName + ">(.*?)</" + propertyName + ">");
        Matcher matcher = pattern.matcher(pomContent);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
    
    private boolean isVersionAtLeast(String actualVersion, String minimumVersion) {
        String[] actualParts = actualVersion.split("\\.");
        String[] minimumParts = minimumVersion.split("\\.");
        
        int maxLength = Math.max(actualParts.length, minimumParts.length);
        
        for (int i = 0; i < maxLength; i++) {
            int actualPart = i < actualParts.length ? Integer.parseInt(actualParts[i]) : 0;
            int minimumPart = i < minimumParts.length ? Integer.parseInt(minimumParts[i]) : 0;
            
            if (actualPart > minimumPart) {
                return true;
            } else if (actualPart < minimumPart) {
                return false;
            }
        }
        
        return true; // versions are equal
    }
}