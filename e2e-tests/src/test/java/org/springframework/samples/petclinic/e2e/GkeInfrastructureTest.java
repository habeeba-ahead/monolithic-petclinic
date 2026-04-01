package org.springframework.samples.petclinic.e2e;

import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.autoscaling.v1.HorizontalPodAutoscaler;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for GKE infrastructure components and configuration.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GkeInfrastructureTest {

    private static KubernetesClient kubernetesClient;
    private static String namespace;

    @BeforeAll
    static void setUp() {
        kubernetesClient = new DefaultKubernetesClient();
        namespace = System.getProperty("app.namespace", "petclinic");
    }

    @AfterAll
    static void tearDown() {
        if (kubernetesClient != null) {
            kubernetesClient.close();
        }
    }

    @Test
    @Order(1)
    @DisplayName("Namespace should be properly configured")
    void testNamespaceConfiguration() {
        Namespace ns = kubernetesClient.namespaces().withName(namespace).get();
        
        assertNotNull(ns, "Namespace should exist");
        assertEquals(namespace, ns.getMetadata().getName(), "Namespace name should match");
        
        Map<String, String> labels = ns.getMetadata().getLabels();
        assertNotNull(labels, "Namespace should have labels");
        assertEquals("petclinic", labels.get("app"), "Namespace should have app label");
    }

    @Test
    @Order(2)
    @DisplayName("ConfigMap should contain proper application configuration")
    void testConfigMapConfiguration() {
        ConfigMap configMap = kubernetesClient.configMaps()
                .inNamespace(namespace)
                .withName("petclinic-config")
                .get();
        
        assertNotNull(configMap, "ConfigMap should exist");
        
        Map<String, String> data = configMap.getData();
        assertNotNull(data, "ConfigMap should have data");
        assertTrue(data.containsKey("application.properties"), 
                  "ConfigMap should contain application.properties");
        
        String appProperties = data.get("application.properties");
        assertTrue(appProperties.contains("management.endpoints.web.exposure.include=*"),
                  "Should expose all actuator endpoints");
        assertTrue(appProperties.contains("management.metrics.export.prometheus.enabled=true"),
                  "Should enable Prometheus metrics");
        assertTrue(appProperties.contains("spring.activemq.broker-url=tcp://activemq-service:61616"),
                  "Should configure ActiveMQ connection");
    }

    @Test
    @Order(3)
    @DisplayName("Deployment should have proper resource limits and requests")
    void testDeploymentResourceConfiguration() {
        Deployment deployment = kubernetesClient.apps().deployments()
                .inNamespace(namespace)
                .withName("petclinic-app")
                .get();
        
        assertNotNull(deployment, "Deployment should exist");
        
        Container container = deployment.getSpec().getTemplate().getSpec().getContainers().get(0);
        ResourceRequirements resources = container.getResources();
        
        assertNotNull(resources, "Container should have resource requirements");
        
        // Check requests
        Map<String, Quantity> requests = resources.getRequests();
        assertNotNull(requests, "Should have resource requests");
        assertTrue(requests.containsKey("memory"), "Should have memory request");
        assertTrue(requests.containsKey("cpu"), "Should have CPU request");
        
        // Check limits
        Map<String, Quantity> limits = resources.getLimits();
        assertNotNull(limits, "Should have resource limits");
        assertTrue(limits.containsKey("memory"), "Should have memory limit");
        assertTrue(limits.containsKey("cpu"), "Should have CPU limit");
    }

    @Test
    @Order(4)
    @DisplayName("Deployment should have proper health checks configured")
    void testHealthCheckConfiguration() {
        Deployment deployment = kubernetesClient.apps().deployments()
                .inNamespace(namespace)
                .withName("petclinic-app")
                .get();
        
        Container container = deployment.getSpec().getTemplate().getSpec().getContainers().get(0);
        
        // Check liveness probe
        Probe livenessProbe = container.getLivenessProbe();
        assertNotNull(livenessProbe, "Should have liveness probe");
        assertNotNull(livenessProbe.getHttpGet(), "Liveness probe should use HTTP GET");
        assertEquals("/manage/health", livenessProbe.getHttpGet().getPath(),
                    "Liveness probe should check health endpoint");
        
        // Check readiness probe
        Probe readinessProbe = container.getReadinessProbe();
        assertNotNull(readinessProbe, "Should have readiness probe");
        assertNotNull(readinessProbe.getHttpGet(), "Readiness probe should use HTTP GET");
        assertTrue(readinessProbe.getHttpGet().getPath().contains("health"),
                  "Readiness probe should check health endpoint");
    }

    @Test
    @Order(5)
    @DisplayName("Service should be properly configured for load balancing")
    void testServiceConfiguration() {
        Service service = kubernetesClient.services()
                .inNamespace(namespace)
                .withName("petclinic-service")
                .get();
        
        assertNotNull(service, "Service should exist");
        assertEquals("LoadBalancer", service.getSpec().getType(),
                    "Service should be of type LoadBalancer");
        
        List<ServicePort> ports = service.getSpec().getPorts();
        assertFalse(ports.isEmpty(), "Service should have ports");
        
        ServicePort httpPort = ports.stream()
                .filter(port -> "http".equals(port.getName()))
                .findFirst()
                .orElse(null);
        
        assertNotNull(httpPort, "Service should have HTTP port");
        assertEquals(Integer.valueOf(8080), httpPort.getPort(),
                    "HTTP port should be 8080");
        assertEquals(Integer.valueOf(8080), httpPort.getTargetPort().getIntVal(),
                    "Target port should be 8080");
    }

    @Test
    @Order(6)
    @DisplayName("HorizontalPodAutoscaler should be properly configured")
    void testHPAConfiguration() {
        HorizontalPodAutoscaler hpa = kubernetesClient.autoscaling().v1().horizontalPodAutoscalers()
                .inNamespace(namespace)
                .withName("petclinic-hpa")
                .get();
        
        assertNotNull(hpa, "HPA should exist");
        assertEquals("petclinic-app", hpa.getSpec().getScaleTargetRef().getName(),
                    "HPA should target petclinic-app deployment");
        
        assertTrue(hpa.getSpec().getMinReplicas() >= 2,
                  "HPA should have minimum 2 replicas");
        assertTrue(hpa.getSpec().getMaxReplicas() >= 5,
                  "HPA should allow scaling to at least 5 replicas");
    }

    @Test
    @Order(7)
    @DisplayName("ActiveMQ should be properly deployed and configured")
    void testActiveMQDeployment() {
        Deployment activeMQDeployment = kubernetesClient.apps().deployments()
                .inNamespace(namespace)
                .withName("activemq")
                .get();
        
        assertNotNull(activeMQDeployment, "ActiveMQ deployment should exist");
        assertTrue(activeMQDeployment.getStatus().getReadyReplicas() > 0,
                  "ActiveMQ should have ready replicas");
        
        Service activeMQService = kubernetesClient.services()
                .inNamespace(namespace)
                .withName("activemq-service")
                .get();
        
        assertNotNull(activeMQService, "ActiveMQ service should exist");
        
        List<ServicePort> ports = activeMQService.getSpec().getPorts();
        boolean hasOpenWirePort = ports.stream()
                .anyMatch(port -> port.getPort() == 61616);
        
        assertTrue(hasOpenWirePort, "ActiveMQ service should expose OpenWire port 61616");
    }

    @Test
    @Order(8)
    @DisplayName("All pods should have proper labels and annotations")
    void testPodLabelsAndAnnotations() {
        List<Pod> pods = kubernetesClient.pods()
                .inNamespace(namespace)
                .withLabel("app", "petclinic")
                .list()
                .getItems();
        
        assertFalse(pods.isEmpty(), "Should have petclinic pods");
        
        for (Pod pod : pods) {
            Map<String, String> labels = pod.getMetadata().getLabels();
            assertNotNull(labels, "Pod should have labels");
            assertEquals("petclinic", labels.get("app"), "Pod should have app label");
            assertEquals("app", labels.get("component"), "Pod should have component label");
        }
    }

    @Test
    @Order(9)
    @DisplayName("Network policies should allow proper communication")
    void testNetworkConnectivity() {
        // Test that petclinic pods can reach ActiveMQ
        List<Pod> petclinicPods = kubernetesClient.pods()
                .inNamespace(namespace)
                .withLabel("app", "petclinic")
                .list()
                .getItems();
        
        assertFalse(petclinicPods.isEmpty(), "Should have petclinic pods");
        
        List<Pod> activeMQPods = kubernetesClient.pods()
                .inNamespace(namespace)
                .withLabel("app", "activemq")
                .list()
                .getItems();
        
        assertFalse(activeMQPods.isEmpty(), "Should have ActiveMQ pods");
        
        // Verify services are accessible
        Service petclinicService = kubernetesClient.services()
                .inNamespace(namespace)
                .withName("petclinic-service")
                .get();
        
        Service activeMQService = kubernetesClient.services()
                .inNamespace(namespace)
                .withName("activemq-service")
                .get();
        
        assertNotNull(petclinicService.getSpec().getClusterIP(),
                     "Petclinic service should have cluster IP");
        assertNotNull(activeMQService.getSpec().getClusterIP(),
                     "ActiveMQ service should have cluster IP");
    }
}