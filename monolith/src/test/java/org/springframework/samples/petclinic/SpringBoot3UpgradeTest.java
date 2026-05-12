package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test to verify Spring Boot 3.x upgrade is successful
 */
@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:hsqldb:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.activemq.broker-url=vm://localhost?broker.persistent=false"
})
public class SpringBoot3UpgradeTest {

    @Test
    public void contextLoads() {
        // This test will pass if the Spring Boot 3.x context loads successfully
        assertTrue(true, "Spring Boot 3.x context should load successfully");
    }

    @Test
    public void verifyJavaVersion() {
        String javaVersion = System.getProperty("java.version");
        // Spring Boot 3.x requires Java 17+
        int majorVersion = Integer.parseInt(javaVersion.split("\\.")[0]);
        assertTrue(majorVersion >= 17,
                  "Java version should be 17 or higher for Spring Boot 3.x, but was: " + javaVersion);
    }
}