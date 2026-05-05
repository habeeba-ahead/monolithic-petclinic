package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.samples.petclinic.service.ClinicService;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test to verify Spring Boot 3.x upgrade is successful
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class SpringBoot3UpgradeTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ClinicService clinicService;

    @Test
    void contextLoads() {
        // This test will pass if the application context loads successfully with Spring Boot 3.x
        // and all jakarta imports are working correctly
        assertThat(applicationContext).isNotNull();
    }

    @Test
    void applicationStartsSuccessfully() {
        // Verify that the application context loads successfully with Spring Boot 3.x
        // This ensures all dependencies are compatible
        assertThat(applicationContext.getBeanDefinitionCount()).isGreaterThan(0);
    }

    @Test
    void clinicServiceIsAvailable() {
        // Verify that our main service is properly wired and available
        assertThat(clinicService).isNotNull();
        
        // Test basic functionality to ensure JPA/Hibernate is working with jakarta
        assertThat(clinicService.petTypes()).isNotNull();
    }

    @Test
    void springBootVersionIs3x() {
        // Verify we're actually running Spring Boot 3.x
        String springBootVersion = org.springframework.boot.SpringBootVersion.getVersion();
        assertThat(springBootVersion).startsWith("3.");
    }
}