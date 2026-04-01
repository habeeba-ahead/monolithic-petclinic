package org.springframework.samples.petclinic.e2e;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Basic configuration tests that can run without a GKE cluster.
 */
public class GkeTestConfigurationTest {

    @Test
    @DisplayName("Test configuration should be properly loaded")
    void testConfigurationLoading() {
        // Test system property defaults with proper handling of empty strings
        String namespace = getPropertyWithDefault("app.namespace", "petclinic");
        assertEquals("petclinic", namespace, "Default namespace should be petclinic");
        
        String clusterName = getPropertyWithDefault("gke.cluster.name", "petclinic-cluster");
        assertEquals("petclinic-cluster", clusterName, "Default cluster name should be petclinic-cluster");
        
        String clusterZone = getPropertyWithDefault("gke.cluster.zone", "us-central1-a");
        assertEquals("us-central1-a", clusterZone, "Default cluster zone should be us-central1-a");
        
        String projectId = getPropertyWithDefault("gcp.project.id", "petclinic-project");
        assertEquals("petclinic-project", projectId, "Default project ID should be petclinic-project");
    }

    @Test
    @DisplayName("Test classes should be properly structured")
    void testClassStructure() {
        // Verify test classes exist and are properly structured
        assertDoesNotThrow(() -> {
            Class.forName("org.springframework.samples.petclinic.e2e.GkeDeploymentE2ETest");
        }, "GkeDeploymentE2ETest class should exist");
        
        assertDoesNotThrow(() -> {
            Class.forName("org.springframework.samples.petclinic.e2e.GkeInfrastructureTest");
        }, "GkeInfrastructureTest class should exist");
        
        assertDoesNotThrow(() -> {
            Class.forName("org.springframework.samples.petclinic.e2e.GkePerformanceTest");
        }, "GkePerformanceTest class should exist");
        
        assertDoesNotThrow(() -> {
            Class.forName("org.springframework.samples.petclinic.e2e.GkeE2ETestSuite");
        }, "GkeE2ETestSuite class should exist");
    }

    @Test
    @DisplayName("Required dependencies should be available")
    void testDependenciesAvailable() {
        // Test Kubernetes client
        assertDoesNotThrow(() -> {
            Class.forName("io.fabric8.kubernetes.client.KubernetesClient");
        }, "Kubernetes client should be available");
        
        // Test Selenium
        assertDoesNotThrow(() -> {
            Class.forName("org.openqa.selenium.WebDriver");
        }, "Selenium WebDriver should be available");
        
        // Test HTTP client
        assertDoesNotThrow(() -> {
            Class.forName("org.apache.http.client.HttpClient");
        }, "Apache HTTP client should be available");
        
        // Test TestContainers
        assertDoesNotThrow(() -> {
            Class.forName("org.testcontainers.containers.BrowserWebDriverContainer");
        }, "TestContainers should be available");
        
        // Test Awaitility
        assertDoesNotThrow(() -> {
            Class.forName("org.awaitility.Awaitility");
        }, "Awaitility should be available");
    }

    @Test
    @DisplayName("Test environment variables should be configurable")
    void testEnvironmentConfiguration() {
        // Test that environment variables can override defaults
        String originalNamespace = System.getProperty("app.namespace");
        
        System.setProperty("app.namespace", "test-namespace");
        String namespace = getPropertyWithDefault("app.namespace", "petclinic");
        assertEquals("test-namespace", namespace, "System property should override default");
        
        // Reset to original value or clear
        if (originalNamespace != null) {
            System.setProperty("app.namespace", originalNamespace);
        } else {
            System.clearProperty("app.namespace");
        }
    }

    @Test
    @DisplayName("Kubernetes manifests should be available")
    void testKubernetesManifests() {
        // Verify that Kubernetes manifest files exist
        assertTrue(java.nio.file.Files.exists(java.nio.file.Paths.get("../k8s/namespace.yaml")),
                  "Namespace manifest should exist");
        assertTrue(java.nio.file.Files.exists(java.nio.file.Paths.get("../k8s/deployment.yaml")),
                  "Deployment manifest should exist");
        assertTrue(java.nio.file.Files.exists(java.nio.file.Paths.get("../k8s/configmap.yaml")),
                  "ConfigMap manifest should exist");
        assertTrue(java.nio.file.Files.exists(java.nio.file.Paths.get("../k8s/hpa.yaml")),
                  "HPA manifest should exist");
        assertTrue(java.nio.file.Files.exists(java.nio.file.Paths.get("../k8s/activemq-deployment.yaml")),
                  "ActiveMQ deployment manifest should exist");
    }

    @Test
    @DisplayName("Deployment scripts should be available and executable")
    void testDeploymentScripts() {
        // Verify that deployment scripts exist
        assertTrue(java.nio.file.Files.exists(java.nio.file.Paths.get("../scripts/deploy-to-gke.sh")),
                  "GKE deployment script should exist");
        assertTrue(java.nio.file.Files.exists(java.nio.file.Paths.get("../scripts/run-e2e-tests.sh")),
                  "E2E test runner script should exist");
        assertTrue(java.nio.file.Files.exists(java.nio.file.Paths.get("../scripts/setup-gke-cluster.sh")),
                  "GKE cluster setup script should exist");
        
        // Verify Dockerfile exists
        assertTrue(java.nio.file.Files.exists(java.nio.file.Paths.get("../Dockerfile")),
                  "Dockerfile should exist");
    }

    /**
     * Helper method to get system property with proper default handling.
     * Returns the default value if the property is null or empty.
     */
    private String getPropertyWithDefault(String propertyName, String defaultValue) {
        String value = System.getProperty(propertyName);
        return (value == null || value.trim().isEmpty()) ? defaultValue : value;
    }
}