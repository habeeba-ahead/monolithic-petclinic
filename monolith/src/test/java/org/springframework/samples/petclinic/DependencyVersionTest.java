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
 * Test to verify that frontend dependencies have been upgraded to eliminate XSS vulnerabilities
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class DependencyVersionTest {

    private static final String POM_XML_PATH = "pom.xml";
    
    @Test
    public void testJQueryVersionIsUpdatedTo3_6Plus() throws IOException {
        String pomContent = readPomXml();
        String jqueryVersion = extractPropertyValue(pomContent, "webjars-jquery.version");
        
        assertNotNull(jqueryVersion, "jQuery version should be defined");
        assertTrue(isVersionAtLeast(jqueryVersion, "3.6.0"), 
            "jQuery version should be 3.6+ but was: " + jqueryVersion);
    }
    
    @Test
    public void testJQueryUIVersionIsUpdatedTo1_13Plus() throws IOException {
        String pomContent = readPomXml();
        String jqueryUIVersion = extractPropertyValue(pomContent, "webjars-jquery-ui.version");
        
        assertNotNull(jqueryUIVersion, "jQuery UI version should be defined");
        assertTrue(isVersionAtLeast(jqueryUIVersion, "1.13.0"), 
            "jQuery UI version should be 1.13+ but was: " + jqueryUIVersion);
    }
    
    @Test
    public void testBootstrapVersionIsUpdatedTo5_x() throws IOException {
        String pomContent = readPomXml();
        String bootstrapVersion = extractPropertyValue(pomContent, "webjars-bootstrap.version");
        
        assertNotNull(bootstrapVersion, "Bootstrap version should be defined");
        assertTrue(isVersionAtLeast(bootstrapVersion, "5.0.0"), 
            "Bootstrap version should be 5.x+ but was: " + bootstrapVersion);
    }
    
    @Test
    public void testAllDependencyVersionsAreSecure() throws IOException {
        String pomContent = readPomXml();
        
        // Extract all versions
        String jqueryVersion = extractPropertyValue(pomContent, "webjars-jquery.version");
        String jqueryUIVersion = extractPropertyValue(pomContent, "webjars-jquery-ui.version");
        String bootstrapVersion = extractPropertyValue(pomContent, "webjars-bootstrap.version");
        
        // Verify all versions meet security requirements
        assertTrue(isVersionAtLeast(jqueryVersion, "3.6.0"), 
            "jQuery version " + jqueryVersion + " does not meet security requirements (3.6+)");
        assertTrue(isVersionAtLeast(jqueryUIVersion, "1.13.0"), 
            "jQuery UI version " + jqueryUIVersion + " does not meet security requirements (1.13+)");
        assertTrue(isVersionAtLeast(bootstrapVersion, "5.0.0"), 
            "Bootstrap version " + bootstrapVersion + " does not meet security requirements (5.x+)");
    }
    
    private String readPomXml() throws IOException {
        return new String(Files.readAllBytes(Paths.get("../../pom.xml")));
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
        if (actualVersion == null || minimumVersion == null) {
            return false;
        }
        
        String[] actualParts = actualVersion.split("\\.");
        String[] minimumParts = minimumVersion.split("\\.");
        
        int maxLength = Math.max(actualParts.length, minimumParts.length);
        
        for (int i = 0; i < maxLength; i++) {
            int actualPart = i < actualParts.length ? parseVersionPart(actualParts[i]) : 0;
            int minimumPart = i < minimumParts.length ? parseVersionPart(minimumParts[i]) : 0;
            
            if (actualPart > minimumPart) {
                return true;
            } else if (actualPart < minimumPart) {
                return false;
            }
        }
        
        return true; // versions are equal
    }
    
    private int parseVersionPart(String versionPart) {
        // Handle version parts that might contain non-numeric suffixes (e.g., "1.0.0-SNAPSHOT")
        String numericPart = versionPart.replaceAll("[^0-9].*", "");
        try {
            return Integer.parseInt(numericPart);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}