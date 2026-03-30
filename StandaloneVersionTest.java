import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Standalone test to verify Spring Boot version from pom.xml
 */
public class StandaloneVersionTest {
    
    public static void main(String[] args) {
        try {
            // Read pom.xml content
            String pomContent = new String(Files.readAllBytes(Paths.get("pom.xml")));
            
            // Extract Spring Boot version using regex
            Pattern pattern = Pattern.compile("<parent>.*?<groupId>org\\.springframework\\.boot</groupId>.*?<artifactId>spring-boot-starter-parent</artifactId>.*?<version>(.*?)</version>.*?</parent>", Pattern.DOTALL);
            Matcher matcher = pattern.matcher(pomContent);
            
            if (matcher.find()) {
                String version = matcher.group(1);
                System.out.println("Found Spring Boot version: " + version);
                
                // Test 1: Check if version starts with 3.2.
                if (version.startsWith("3.2.")) {
                    System.out.println("✓ Spring Boot version is 3.2.x");
                } else {
                    System.out.println("✗ Spring Boot version is NOT 3.2.x");
                    System.exit(1);
                }
                
                // Test 2: Check specific version
                if ("3.2.12".equals(version)) {
                    System.out.println("✓ Spring Boot version is exactly 3.2.12");
                } else {
                    System.out.println("! Spring Boot version is " + version + " (expected 3.2.12)");
                }
                
                System.out.println("✓ All version tests passed!");
                
            } else {
                System.out.println("✗ Could not find Spring Boot parent version in pom.xml");
                System.exit(1);
            }
            
        } catch (IOException e) {
            System.out.println("✗ Error reading pom.xml: " + e.getMessage());
            System.exit(1);
        }
    }
}