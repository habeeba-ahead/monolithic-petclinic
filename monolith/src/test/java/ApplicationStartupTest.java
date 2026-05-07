import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.samples.petclinic.Application;

/**
 * Test to verify that the Spring Boot 3.x application can start up successfully
 */
@SpringBootTest(classes = Application.class)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:hsqldb:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
public class ApplicationStartupTest {

    @Test
    public void contextLoads() {
        // This test will pass if the Spring context loads successfully
        // which verifies that all Spring 3.x dependencies are compatible
        System.out.println("✓ Spring Boot 3.x application context loaded successfully");
    }
}