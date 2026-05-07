import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringBootVersion;
import org.springframework.core.SpringVersion;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test to verify Spring 3.x migration acceptance criteria
 */
public class Spring3xMigrationTest {

    @Test
    public void testSpringBootVersion() {
        String version = SpringBootVersion.getVersion();
        assertNotNull(version, "Spring Boot version should not be null");
        assertTrue(version.startsWith("3."), 
            "Spring Boot version should be 3.x, but was: " + version);
        System.out.println("✓ Spring Boot version: " + version);
    }

    @Test
    public void testSpringFrameworkVersion() {
        String version = SpringVersion.getVersion();
        assertNotNull(version, "Spring Framework version should not be null");
        assertTrue(version.startsWith("6."), 
            "Spring Framework version should be 6.x (compatible with Spring Boot 3.x), but was: " + version);
        System.out.println("✓ Spring Framework version: " + version);
    }

    @Test
    public void testJakartaNamespaceUsage() {
        // Test that Jakarta EE namespaces are being used instead of javax
        try {
            Class.forName("jakarta.persistence.Entity");
            System.out.println("✓ Jakarta Persistence API is available");
        } catch (ClassNotFoundException e) {
            fail("Jakarta Persistence API should be available for Spring Boot 3.x");
        }

        try {
            Class.forName("jakarta.annotation.PostConstruct");
            System.out.println("✓ Jakarta Annotations API is available");
        } catch (ClassNotFoundException e) {
            fail("Jakarta Annotations API should be available for Spring Boot 3.x");
        }

        try {
            Class.forName("jakarta.jms.Message");
            System.out.println("✓ Jakarta JMS API is available");
        } catch (ClassNotFoundException e) {
            fail("Jakarta JMS API should be available for Spring Boot 3.x");
        }
    }

    @Test
    public void testJava17Compatibility() {
        String javaVersion = System.getProperty("java.version");
        assertNotNull(javaVersion, "Java version should not be null");
        
        // Spring Boot 3.x requires Java 17+
        String majorVersion = javaVersion.split("\\.")[0];
        int majorVersionInt = Integer.parseInt(majorVersion);
        assertTrue(majorVersionInt >= 17, 
            "Java version should be 17 or higher for Spring Boot 3.x, but was: " + javaVersion);
        System.out.println("✓ Java version: " + javaVersion);
    }
}