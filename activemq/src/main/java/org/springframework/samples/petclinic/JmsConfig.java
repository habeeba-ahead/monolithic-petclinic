package org.springframework.samples.petclinic;

import org.apache.activemq.artemis.core.config.impl.ConfigurationImpl;
import org.apache.activemq.artemis.core.server.ActiveMQServer;
import org.apache.activemq.artemis.core.server.ActiveMQServers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JmsConfig {

    @Value("${spring.artemis.port:61616}")
    int brokerPort;

    @Bean
    ActiveMQServer artemisServer() throws Exception {
        ConfigurationImpl configuration = new ConfigurationImpl();
        configuration.setPersistenceEnabled(false);
        configuration.setJournalDirectory("target/data/journal");
        configuration.setSecurityEnabled(false);
        configuration.addAcceptorConfiguration("tcp", "tcp://0.0.0.0:" + brokerPort);
        
        ActiveMQServer server = ActiveMQServers.newActiveMQServer(configuration);
        return server;
    }
}