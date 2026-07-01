package org.springframework.samples.petclinic;

import org.apache.activemq.broker.BrokerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for JmsConfig to verify Spring Boot 2.7.x compatibility.
 */
@SpringBootTest
@TestPropertySource(properties = {
    "activemq.broker-url=vm://localhost?broker.persistent=false",
    "server.port=0"
})
class JmsConfigTest {

    @Autowired
    private BrokerService brokerService;

    @Test
    void brokerServiceBeanShouldBeCreated() {
        assertThat(brokerService).isNotNull();
    }

    @Test
    void brokerServiceShouldNotBePersistent() {
        assertThat(brokerService.isPersistent()).isFalse();
    }

    @Test
    void brokerServiceShouldUseJmx() {
        assertThat(brokerService.isUseJmx()).isTrue();
    }
}
