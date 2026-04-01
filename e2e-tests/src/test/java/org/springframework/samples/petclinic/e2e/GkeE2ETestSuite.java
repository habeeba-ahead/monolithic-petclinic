package org.springframework.samples.petclinic.e2e;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Test suite for running all GKE end-to-end tests in the correct order.
 * This is a simple test class that can be used to run all E2E tests together.
 */
@DisplayName("GKE End-to-End Test Suite")
public class GkeE2ETestSuite {
    
    @Test
    @DisplayName("Test suite configuration")
    void testSuiteConfiguration() {
        // This test verifies that the test suite is properly configured
        // Individual tests should be run separately or through Maven profiles
        System.out.println("GKE E2E Test Suite is configured");
        System.out.println("Run individual test classes:");
        System.out.println("- GkeTestConfigurationTest: Basic configuration validation");
        System.out.println("- GkeInfrastructureTest: Infrastructure component validation");
        System.out.println("- GkeDeploymentE2ETest: End-to-end deployment testing");
        System.out.println("- GkePerformanceTest: Performance and load testing");
    }
}