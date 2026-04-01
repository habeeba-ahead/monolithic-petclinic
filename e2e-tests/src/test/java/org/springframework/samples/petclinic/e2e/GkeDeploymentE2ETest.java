package org.springframework.samples.petclinic.e2e;

import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testcontainers.containers.BrowserWebDriverContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end tests for GKE deployment of Pet Clinic application.
 * Tests the complete deployment pipeline from code to running application.
 */
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GkeDeploymentE2ETest {

    private static KubernetesClient kubernetesClient;
    private static String namespace;
    private static String clusterName;
    private static String clusterZone;
    private static String projectId;
    private static String applicationUrl;

    @Container
    static BrowserWebDriverContainer<?> chrome = new BrowserWebDriverContainer<>()
            .withCapabilities(new ChromeOptions());

    @BeforeAll
    static void setUp() {
        // Initialize Kubernetes client
        kubernetesClient = new DefaultKubernetesClient();
        
        // Get configuration from system properties
        namespace = System.getProperty("app.namespace", "petclinic");
        clusterName = System.getProperty("gke.cluster.name", "petclinic-cluster");
        clusterZone = System.getProperty("gke.cluster.zone", "us-central1-a");
        projectId = System.getProperty("gcp.project.id", "petclinic-project");
        
        // Verify cluster connectivity
        assertNotNull(kubernetesClient, "Kubernetes client should be initialized");
        assertTrue(kubernetesClient.namespaces().list().getItems().size() > 0, 
                  "Should be able to connect to Kubernetes cluster");
    }

    @AfterAll
    static void tearDown() {
        if (kubernetesClient != null) {
            kubernetesClient.close();
        }
    }

    @Test
    @Order(1)
    @DisplayName("Application should deploy successfully to GKE cluster")
    void testApplicationDeployment() {
        // Verify namespace exists
        Namespace ns = kubernetesClient.namespaces().withName(namespace).get();
        if (ns == null) {
            fail("Namespace '" + namespace + "' does not exist. Deployment may have failed.");
        }

        // Verify deployment exists and is ready
        Deployment deployment = kubernetesClient.apps().deployments()
                .inNamespace(namespace)
                .withName("petclinic-app")
                .get();
        
        assertNotNull(deployment, "Pet Clinic deployment should exist");
        
        // Wait for deployment to be ready
        await().atMost(5, TimeUnit.MINUTES)
               .pollInterval(10, TimeUnit.SECONDS)
               .until(() -> {
                   Deployment currentDeployment = kubernetesClient.apps().deployments()
                           .inNamespace(namespace)
                           .withName("petclinic-app")
                           .get();
                   return currentDeployment != null && 
                          currentDeployment.getStatus().getReadyReplicas() != null &&
                          currentDeployment.getStatus().getReadyReplicas() > 0;
               });

        // Verify deployment status
        deployment = kubernetesClient.apps().deployments()
                .inNamespace(namespace)
                .withName("petclinic-app")
                .get();
        
        assertTrue(deployment.getStatus().getReadyReplicas() > 0, 
                  "At least one replica should be ready");
        assertEquals(deployment.getStatus().getReplicas(), 
                    deployment.getStatus().getReadyReplicas(),
                    "All replicas should be ready");
    }

    @Test
    @Order(2)
    @DisplayName("All pods should start and pass health checks")
    void testPodsHealthChecks() {
        // Get all pods for the application
        List<Pod> pods = kubernetesClient.pods()
                .inNamespace(namespace)
                .withLabel("app", "petclinic")
                .list()
                .getItems();
        
        assertFalse(pods.isEmpty(), "Should have at least one pod running");
        
        // Verify each pod is running and ready
        for (Pod pod : pods) {
            assertEquals("Running", pod.getStatus().getPhase(), 
                        "Pod " + pod.getMetadata().getName() + " should be running");
            
            // Check readiness probe
            List<ContainerStatus> containerStatuses = pod.getStatus().getContainerStatuses();
            assertFalse(containerStatuses.isEmpty(), "Pod should have container statuses");
            
            for (ContainerStatus status : containerStatuses) {
                assertTrue(status.getReady(), 
                          "Container " + status.getName() + " should be ready");
                assertEquals(0, status.getRestartCount().intValue(),
                           "Container should not have restarted");
            }
        }
        
        // Verify health check endpoints
        Service service = kubernetesClient.services()
                .inNamespace(namespace)
                .withName("petclinic-service")
                .get();
        
        assertNotNull(service, "Pet Clinic service should exist");
        
        // Get service external IP or LoadBalancer IP
        String serviceIP = getServiceExternalIP(service);
        assertNotNull(serviceIP, "Service should have external IP");
        
        applicationUrl = "http://" + serviceIP + ":8080";
        
        // Test health endpoint
        await().atMost(2, TimeUnit.MINUTES)
               .pollInterval(5, TimeUnit.SECONDS)
               .until(() -> {
                   try {
                       HttpClient client = HttpClients.createDefault();
                       HttpGet request = new HttpGet(applicationUrl + "/manage/health");
                       HttpResponse response = client.execute(request);
                       return response.getStatusLine().getStatusCode() == 200;
                   } catch (Exception e) {
                       return false;
                   }
               });
    }

    @Test
    @Order(3)
    @DisplayName("Database connectivity should work correctly")
    void testDatabaseConnectivity() {
        // Verify database deployment
        Deployment dbDeployment = kubernetesClient.apps().deployments()
                .inNamespace(namespace)
                .withName("petclinic-db")
                .get();
        
        if (dbDeployment != null) {
            // External database deployment
            assertTrue(dbDeployment.getStatus().getReadyReplicas() > 0,
                      "Database deployment should be ready");
        }
        
        // Test database connectivity through application
        try {
            HttpClient client = HttpClients.createDefault();
            HttpGet request = new HttpGet(applicationUrl + "/owners");
            HttpResponse response = client.execute(request);
            
            assertEquals(200, response.getStatusLine().getStatusCode(),
                        "Should be able to access owners page (requires DB)");
            
            String content = EntityUtils.toString(response.getEntity());
            assertTrue(content.contains("Owners"), 
                      "Page should contain owners data from database");
            
        } catch (Exception e) {
            fail("Failed to test database connectivity: " + e.getMessage());
        }
    }

    @Test
    @Order(4)
    @DisplayName("Application functionality should be verified through UI testing")
    void testApplicationFunctionality() {
        WebDriver driver = chrome.getWebDriver();
        
        try {
            // Navigate to application
            driver.get(applicationUrl);
            
            // Verify home page loads
            WebDriverWait wait = new WebDriverWait(driver, 30);
            wait.until(webDriver -> webDriver.getTitle().contains("PetClinic"));
            
            assertTrue(driver.getTitle().contains("PetClinic"), 
                      "Home page should load with correct title");
            
            // Test navigation to owners
            WebElement ownersLink = driver.findElement(By.linkText("FIND OWNERS"));
            ownersLink.click();
            
            wait.until(webDriver -> webDriver.getCurrentUrl().contains("owners"));
            assertTrue(driver.getCurrentUrl().contains("owners"),
                      "Should navigate to owners page");
            
            // Test search functionality
            WebElement searchButton = driver.findElement(By.xpath("//button[contains(text(),'Find Owner')]"));
            searchButton.click();
            
            wait.until(webDriver -> webDriver.getPageSource().contains("George Franklin") ||
                                   webDriver.getPageSource().contains("Betty Davis"));
            
            assertTrue(driver.getPageSource().contains("George Franklin") ||
                      driver.getPageSource().contains("Betty Davis"),
                      "Should display owner search results");
            
            // Test veterinarians page
            driver.get(applicationUrl + "/vets");
            wait.until(webDriver -> webDriver.getPageSource().contains("James Carter") ||
                                   webDriver.getPageSource().contains("Helen Leary"));
            
            assertTrue(driver.getPageSource().contains("Veterinarians"),
                      "Veterinarians page should load");
            
        } catch (Exception e) {
            fail("UI functionality test failed: " + e.getMessage());
        }
    }

    @Test
    @Order(5)
    @DisplayName("Metrics and logs should be collected properly")
    void testMetricsAndLogging() {
        // Test actuator endpoints
        try {
            HttpClient client = HttpClients.createDefault();
            
            // Test metrics endpoint
            HttpGet metricsRequest = new HttpGet(applicationUrl + "/manage/metrics");
            HttpResponse metricsResponse = client.execute(metricsRequest);
            assertEquals(200, metricsResponse.getStatusLine().getStatusCode(),
                        "Metrics endpoint should be accessible");
            
            String metricsContent = EntityUtils.toString(metricsResponse.getEntity());
            assertTrue(metricsContent.contains("jvm.memory.used") ||
                      metricsContent.contains("http.server.requests"),
                      "Metrics should contain JVM or HTTP metrics");
            
            // Test info endpoint
            HttpGet infoRequest = new HttpGet(applicationUrl + "/manage/info");
            HttpResponse infoResponse = client.execute(infoRequest);
            assertEquals(200, infoResponse.getStatusLine().getStatusCode(),
                        "Info endpoint should be accessible");
            
        } catch (Exception e) {
            fail("Failed to test metrics endpoints: " + e.getMessage());
        }
        
        // Verify pod logs are being collected
        List<Pod> pods = kubernetesClient.pods()
                .inNamespace(namespace)
                .withLabel("app", "petclinic")
                .list()
                .getItems();
        
        assertFalse(pods.isEmpty(), "Should have pods to check logs");
        
        for (Pod pod : pods) {
            String logs = kubernetesClient.pods()
                    .inNamespace(namespace)
                    .withName(pod.getMetadata().getName())
                    .getLog();
            
            assertNotNull(logs, "Pod logs should be available");
            assertFalse(logs.trim().isEmpty(), "Pod logs should not be empty");
            assertTrue(logs.contains("Started Application") || 
                      logs.contains("Tomcat started"),
                      "Logs should contain application startup messages");
        }
    }

    @Test
    @Order(6)
    @DisplayName("Horizontal scaling should work correctly")
    void testHorizontalScaling() {
        String deploymentName = "petclinic-app";
        
        // Get current replica count
        Deployment deployment = kubernetesClient.apps().deployments()
                .inNamespace(namespace)
                .withName(deploymentName)
                .get();
        
        assertNotNull(deployment, "Deployment should exist");
        int originalReplicas = deployment.getSpec().getReplicas();
        int targetReplicas = originalReplicas + 1;
        
        // Scale up
        kubernetesClient.apps().deployments()
                .inNamespace(namespace)
                .withName(deploymentName)
                .scale(targetReplicas);
        
        // Wait for scaling to complete
        await().atMost(3, TimeUnit.MINUTES)
               .pollInterval(10, TimeUnit.SECONDS)
               .until(() -> {
                   Deployment currentDeployment = kubernetesClient.apps().deployments()
                           .inNamespace(namespace)
                           .withName(deploymentName)
                           .get();
                   return currentDeployment.getStatus().getReadyReplicas() != null &&
                          currentDeployment.getStatus().getReadyReplicas() == targetReplicas;
               });
        
        // Verify scaling
        deployment = kubernetesClient.apps().deployments()
                .inNamespace(namespace)
                .withName(deploymentName)
                .get();
        
        assertEquals(targetReplicas, deployment.getStatus().getReadyReplicas().intValue(),
                    "Should have scaled to target replica count");
        
        // Verify all pods are running
        List<Pod> pods = kubernetesClient.pods()
                .inNamespace(namespace)
                .withLabel("app", "petclinic")
                .list()
                .getItems();
        
        assertEquals(targetReplicas, pods.size(),
                    "Should have correct number of pods after scaling");
        
        for (Pod pod : pods) {
            assertEquals("Running", pod.getStatus().getPhase(),
                        "All pods should be running after scaling");
        }
        
        // Test application still works with multiple replicas
        try {
            HttpClient client = HttpClients.createDefault();
            
            // Make multiple requests to test load balancing
            for (int i = 0; i < 5; i++) {
                HttpGet request = new HttpGet(applicationUrl + "/");
                HttpResponse response = client.execute(request);
                assertEquals(200, response.getStatusLine().getStatusCode(),
                            "Application should respond correctly with multiple replicas");
            }
            
        } catch (Exception e) {
            fail("Application failed to respond correctly after scaling: " + e.getMessage());
        }
        
        // Scale back down
        kubernetesClient.apps().deployments()
                .inNamespace(namespace)
                .withName(deploymentName)
                .scale(originalReplicas);
        
        // Wait for scale down
        await().atMost(2, TimeUnit.MINUTES)
               .pollInterval(10, TimeUnit.SECONDS)
               .until(() -> {
                   Deployment currentDeployment = kubernetesClient.apps().deployments()
                           .inNamespace(namespace)
                           .withName(deploymentName)
                           .get();
                   return currentDeployment.getStatus().getReadyReplicas() != null &&
                          currentDeployment.getStatus().getReadyReplicas() == originalReplicas;
               });
    }

    private String getServiceExternalIP(Service service) {
        // Try to get LoadBalancer IP
        if (service.getStatus() != null && 
            service.getStatus().getLoadBalancer() != null &&
            service.getStatus().getLoadBalancer().getIngress() != null &&
            !service.getStatus().getLoadBalancer().getIngress().isEmpty()) {
            
            LoadBalancerIngress ingress = service.getStatus().getLoadBalancer().getIngress().get(0);
            if (ingress.getIp() != null) {
                return ingress.getIp();
            }
            if (ingress.getHostname() != null) {
                return ingress.getHostname();
            }
        }
        
        // Fallback to external IP
        if (service.getSpec().getExternalIPs() != null && 
            !service.getSpec().getExternalIPs().isEmpty()) {
            return service.getSpec().getExternalIPs().get(0);
        }
        
        // For testing, try to get cluster IP
        return service.getSpec().getClusterIP();
    }
}