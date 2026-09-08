package org.springframework.samples.petclinic;

import org.apache.activemq.broker.BrokerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests verifying that JMX is disabled on the ActiveMQ broker and that
 * Hawtio authentication is enabled, eliminating unauthenticated broker
 * administrative access.
 */
@SpringBootTest
class JmsConfigSecurityTest {

    @Autowired
    private BrokerService brokerService;

    /**
     * Verifies that the embedded ActiveMQ broker has JMX disabled.
     * JMX must be off to prevent unauthenticated remote management access.
     */
    @Test
    void brokerShouldHaveJmxDisabled() {
        assertFalse(brokerService.isUseJmx(),
                "BrokerService.isUseJmx() must return false – unauthenticated JMX access is a high-severity vulnerability");
    }

    /**
     * Verifies that application.properties declares hawtio.authenticationEnabled=true.
     * This ensures the Hawtio console requires login before granting access.
     */
    @Test
    void applicationPropertiesShouldEnableHawtioAuthentication() throws IOException {
        ClassPathResource resource = new ClassPathResource("application.properties");
        assertTrue(resource.exists(), "application.properties must exist on the classpath");

        Properties props = new Properties();
        try (InputStream is = resource.getInputStream()) {
            props.load(is);
        }

        String value = props.getProperty("hawtio.authenticationEnabled");
        assertNotNull(value, "hawtio.authenticationEnabled must be set in application.properties");
        assertEquals("true", value.trim(),
                "hawtio.authenticationEnabled must be 'true' to secure the Hawtio console");
    }
}
