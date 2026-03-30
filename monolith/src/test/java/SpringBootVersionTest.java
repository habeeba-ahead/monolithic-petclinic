import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringBootVersion;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test to verify Spring Boot version is 3.2.x
 */
public class SpringBootVersionTest {

    @Test
    public void testSpringBootVersionIs3_2_x() {
        String version = SpringBootVersion.getVersion();
        assertNotNull(version, "Spring Boot version should not be null");
        assertTrue(version.startsWith("3.2."), 
            "Spring Boot version should start with '3.2.' but was: " + version);
        System.out.println("✓ Spring Boot version verified: " + version);
    }
    
    @Test
    public void testSpringBootVersionIsSpecific() {
        String version = SpringBootVersion.getVersion();
        assertEquals("3.2.12", version, 
            "Expected Spring Boot version 3.2.12 but found: " + version);
        System.out.println("✓ Specific Spring Boot version verified: " + version);
    }
}