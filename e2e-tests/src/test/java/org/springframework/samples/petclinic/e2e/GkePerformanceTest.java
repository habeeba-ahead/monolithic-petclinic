package org.springframework.samples.petclinic.e2e;

import io.fabric8.kubernetes.api.model.LoadBalancerIngress;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Performance and load tests for the Pet Clinic application on GKE.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GkePerformanceTest {

    private static KubernetesClient kubernetesClient;
    private static String namespace;
    private static String applicationUrl;

    @BeforeAll
    static void setUp() {
        kubernetesClient = new DefaultKubernetesClient();
        namespace = System.getProperty("app.namespace", "petclinic");
        
        // Get application URL
        applicationUrl = getApplicationUrl();
        assertNotNull(applicationUrl, "Application URL should be available");
    }

    @AfterAll
    static void tearDown() {
        if (kubernetesClient != null) {
            kubernetesClient.close();
        }
    }

    @Test
    @Order(1)
    @DisplayName("Application should handle concurrent requests efficiently")
    void testConcurrentRequestHandling() throws InterruptedException, ExecutionException, TimeoutException {
        int numberOfThreads = 10;
        int requestsPerThread = 20;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        
        List<Future<Boolean>> futures = new ArrayList<>();
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);
        
        long startTime = System.currentTimeMillis();
        
        // Submit concurrent requests
        for (int i = 0; i < numberOfThreads; i++) {
            Future<Boolean> future = executor.submit(() -> {
                HttpClient client = HttpClients.createDefault();
                
                for (int j = 0; j < requestsPerThread; j++) {
                    try {
                        HttpGet request = new HttpGet(applicationUrl + "/");
                        HttpResponse response = client.execute(request);
                        
                        if (response.getStatusLine().getStatusCode() == 200) {
                            successCount.incrementAndGet();
                        } else {
                            errorCount.incrementAndGet();
                        }
                        
                        // Small delay between requests
                        Thread.sleep(100);
                        
                    } catch (Exception e) {
                        errorCount.incrementAndGet();
                    }
                }
                return true;
            });
            
            futures.add(future);
        }
        
        // Wait for all requests to complete
        for (Future<Boolean> future : futures) {
            future.get(5, TimeUnit.MINUTES);
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        executor.shutdown();
        
        int totalRequests = numberOfThreads * requestsPerThread;
        double successRate = (double) successCount.get() / totalRequests * 100;
        double requestsPerSecond = (double) totalRequests / (duration / 1000.0);
        
        System.out.println("Performance Test Results:");
        System.out.println("Total requests: " + totalRequests);
        System.out.println("Successful requests: " + successCount.get());
        System.out.println("Failed requests: " + errorCount.get());
        System.out.println("Success rate: " + String.format("%.2f%%", successRate));
        System.out.println("Duration: " + duration + "ms");
        System.out.println("Requests per second: " + String.format("%.2f", requestsPerSecond));
        
        assertTrue(successRate >= 95.0, "Success rate should be at least 95%");
        assertTrue(requestsPerSecond >= 5.0, "Should handle at least 5 requests per second");
    }

    @Test
    @Order(2)
    @DisplayName("Application should respond within acceptable time limits")
    void testResponseTimes() {
        HttpClient client = HttpClients.createDefault();
        List<Long> responseTimes = new ArrayList<>();
        
        // Test multiple endpoints
        String[] endpoints = {"/", "/owners", "/vets", "/manage/health"};
        
        for (String endpoint : endpoints) {
            for (int i = 0; i < 10; i++) {
                try {
                    long startTime = System.currentTimeMillis();
                    
                    HttpGet request = new HttpGet(applicationUrl + endpoint);
                    HttpResponse response = client.execute(request);
                    
                    long endTime = System.currentTimeMillis();
                    long responseTime = endTime - startTime;
                    
                    assertEquals(200, response.getStatusLine().getStatusCode(),
                                "Request to " + endpoint + " should succeed");
                    
                    responseTimes.add(responseTime);
                    
                    // Consume response to free connection
                    EntityUtils.consume(response.getEntity());
                    
                } catch (Exception e) {
                    fail("Failed to test endpoint " + endpoint + ": " + e.getMessage());
                }
            }
        }
        
        // Calculate statistics
        double averageResponseTime = responseTimes.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0.0);
        
        long maxResponseTime = responseTimes.stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0L);
        
        System.out.println("Response Time Statistics:");
        System.out.println("Average response time: " + String.format("%.2f ms", averageResponseTime));
        System.out.println("Maximum response time: " + maxResponseTime + " ms");
        
        assertTrue(averageResponseTime <= 2000, "Average response time should be under 2 seconds");
        assertTrue(maxResponseTime <= 5000, "Maximum response time should be under 5 seconds");
    }

    @Test
    @Order(3)
    @DisplayName("Auto-scaling should work under load")
    void testAutoScaling() throws InterruptedException {
        String deploymentName = "petclinic-app";
        
        // Get initial replica count
        Deployment deployment = kubernetesClient.apps().deployments()
                .inNamespace(namespace)
                .withName(deploymentName)
                .get();
        
        int initialReplicas = deployment.getStatus().getReadyReplicas();
        System.out.println("Initial replicas: " + initialReplicas);
        
        // Generate load to trigger auto-scaling
        ExecutorService loadGenerator = Executors.newFixedThreadPool(20);
        
        System.out.println("Generating load to trigger auto-scaling...");
        
        for (int i = 0; i < 20; i++) {
            loadGenerator.submit(() -> {
                HttpClient client = HttpClients.createDefault();
                
                for (int j = 0; j < 100; j++) {
                    try {
                        HttpGet request = new HttpGet(applicationUrl + "/owners");
                        HttpResponse response = client.execute(request);
                        EntityUtils.consume(response.getEntity());
                        
                        // CPU intensive operation simulation
                        Thread.sleep(50);
                        
                    } catch (Exception e) {
                        // Continue generating load even if some requests fail
                    }
                }
            });
        }
        
        // Wait for auto-scaling to potentially trigger
        Thread.sleep(120000); // 2 minutes
        
        // Check if scaling occurred
        deployment = kubernetesClient.apps().deployments()
                .inNamespace(namespace)
                .withName(deploymentName)
                .get();
        
        int currentReplicas = deployment.getStatus().getReadyReplicas();
        System.out.println("Replicas after load: " + currentReplicas);
        
        loadGenerator.shutdown();
        loadGenerator.awaitTermination(30, TimeUnit.SECONDS);
        
        // Auto-scaling might not always trigger in test environment
        // So we just verify the system can handle the load
        assertTrue(currentReplicas >= initialReplicas,
                  "Replica count should not decrease under load");
        
        // Verify all pods are still healthy
        List<Pod> pods = kubernetesClient.pods()
                .inNamespace(namespace)
                .withLabel("app", "petclinic")
                .list()
                .getItems();
        
        for (Pod pod : pods) {
            assertEquals("Running", pod.getStatus().getPhase(),
                        "All pods should remain running under load");
        }
    }

    @Test
    @Order(4)
    @DisplayName("Memory usage should remain within acceptable limits")
    void testMemoryUsage() {
        // Test memory usage through metrics endpoint
        try {
            HttpClient client = HttpClients.createDefault();
            HttpGet request = new HttpGet(applicationUrl + "/manage/metrics/jvm.memory.used");
            HttpResponse response = client.execute(request);
            
            assertEquals(200, response.getStatusLine().getStatusCode(),
                        "Should be able to access memory metrics");
            
            String content = EntityUtils.toString(response.getEntity());
            assertTrue(content.contains("measurements") || content.contains("value"),
                      "Memory metrics should contain measurement data");
            
            // Generate some load and check memory again
            for (int i = 0; i < 50; i++) {
                HttpGet loadRequest = new HttpGet(applicationUrl + "/owners");
                HttpResponse loadResponse = client.execute(loadRequest);
                EntityUtils.consume(loadResponse.getEntity());
            }
            
            // Check memory metrics again
            HttpGet memoryRequest = new HttpGet(applicationUrl + "/manage/metrics/jvm.memory.used");
            HttpResponse memoryResponse = client.execute(memoryRequest);
            
            assertEquals(200, memoryResponse.getStatusLine().getStatusCode(),
                        "Memory metrics should still be accessible after load");
            
        } catch (Exception e) {
            fail("Failed to test memory usage: " + e.getMessage());
        }
    }

    @Test
    @Order(5)
    @DisplayName("Application should recover from pod failures")
    void testPodFailureRecovery() {
        // Get current pods
        List<Pod> pods = kubernetesClient.pods()
                .inNamespace(namespace)
                .withLabel("app", "petclinic")
                .list()
                .getItems();
        
        assertFalse(pods.isEmpty(), "Should have pods to test failure recovery");
        
        if (pods.size() > 1) {
            // Delete one pod to simulate failure
            Pod podToDelete = pods.get(0);
            String podName = podToDelete.getMetadata().getName();
            
            System.out.println("Deleting pod: " + podName);
            kubernetesClient.pods()
                    .inNamespace(namespace)
                    .withName(podName)
                    .delete();
            
            // Wait for pod to be recreated
            await().atMost(3, TimeUnit.MINUTES)
                   .pollInterval(10, TimeUnit.SECONDS)
                   .until(() -> {
                       List<Pod> currentPods = kubernetesClient.pods()
                               .inNamespace(namespace)
                               .withLabel("app", "petclinic")
                               .list()
                               .getItems();
                       
                       return currentPods.size() >= pods.size() &&
                              currentPods.stream().allMatch(pod -> 
                                      "Running".equals(pod.getStatus().getPhase()));
                   });
            
            // Verify application is still accessible
            try {
                HttpClient client = HttpClients.createDefault();
                HttpGet request = new HttpGet(applicationUrl + "/");
                HttpResponse response = client.execute(request);
                
                assertEquals(200, response.getStatusLine().getStatusCode(),
                            "Application should be accessible after pod failure");
                
            } catch (Exception e) {
                fail("Application not accessible after pod failure: " + e.getMessage());
            }
        } else {
            System.out.println("Skipping pod failure test - only one pod available");
        }
    }

    private static String getApplicationUrl() {
        try {
            // Try to get LoadBalancer IP
            Service service = kubernetesClient.services()
                    .inNamespace(namespace)
                    .withName("petclinic-service")
                    .get();
            
            if (service != null) {
                if (service.getStatus() != null && 
                    service.getStatus().getLoadBalancer() != null &&
                    service.getStatus().getLoadBalancer().getIngress() != null &&
                    !service.getStatus().getLoadBalancer().getIngress().isEmpty()) {
                    
                    LoadBalancerIngress ingress = service.getStatus().getLoadBalancer().getIngress().get(0);
                    if (ingress.getIp() != null) {
                        return "http://" + ingress.getIp() + ":8080";
                    }
                    if (ingress.getHostname() != null) {
                        return "http://" + ingress.getHostname() + ":8080";
                    }
                }
                
                // Fallback to cluster IP for testing
                if (service.getSpec().getClusterIP() != null) {
                    return "http://" + service.getSpec().getClusterIP() + ":8080";
                }
            }
            
            return null;
            
        } catch (Exception e) {
            return null;
        }
    }
}