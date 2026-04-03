import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Test to verify Spring Boot 2.7.x upgrade works correctly
 */
@SpringBootTest
@TestPropertySource(properties = {
    "spring.sql.init.mode=always",
    "spring.jpa.defer-datasource-initialization=true"
})
public class SpringBootUpgradeTest {

    @Test
    public void contextLoads() {
        // This test will pass if the Spring context loads successfully
        // with Spring Boot 2.7.x
    }
}