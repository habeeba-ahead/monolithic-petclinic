package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringBootTest
@SpringJUnitConfig
public class ApplicationSecurityIntegrationTest {

    @Test
    public void applicationShouldStartWithSpringSecurity() {
        // This test will pass if the Spring context loads successfully
        // with Spring Security enabled
    }
}