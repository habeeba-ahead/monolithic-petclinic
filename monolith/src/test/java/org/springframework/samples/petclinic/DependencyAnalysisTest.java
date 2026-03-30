package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Test class to verify Spring Boot dependency analysis completeness
 * This test ensures all critical dependencies are documented for migration
 */
public class DependencyAnalysisTest {

    private static final String ANALYSIS_FILE = "../spring-boot-dependency-analysis.md";
    private static final String ROOT_POM = "../pom.xml";
    private static final String MONOLITH_POM = "pom.xml";
    private static final String ACTIVEMQ_POM = "../activemq/pom.xml";

    @Test
    @DisplayName("Analysis file should exist and contain required sections")
    public void testAnalysisFileExists() throws IOException {
        assertTrue(Files.exists(Paths.get(ANALYSIS_FILE)), 
            "Spring Boot dependency analysis file should exist");
        
        String content = Files.readString(Paths.get(ANALYSIS_FILE));
        
        // Verify required sections exist
        assertTrue(content.contains("Current Spring Boot Dependencies Inventory"), 
            "Should contain current dependencies inventory");
        assertTrue(content.contains("Spring Boot 3.2.x Compatibility Matrix"), 
            "Should contain compatibility matrix");
        assertTrue(content.contains("Critical Breaking Changes"), 
            "Should contain breaking changes section");
        assertTrue(content.contains("Migration Strategy"), 
            "Should contain migration strategy");
    }

    @Test
    @DisplayName("Should document Spring Boot version correctly")
    public void testSpringBootVersionDocumented() throws IOException {
        String rootPom = Files.readString(Paths.get(ROOT_POM));
        String analysis = Files.readString(Paths.get(ANALYSIS_FILE));
        
        assertTrue(rootPom.contains("2.2.1.RELEASE"), 
            "Root POM should contain Spring Boot 2.2.1.RELEASE");
        assertTrue(analysis.contains("2.2.1.RELEASE"), 
            "Analysis should document current Spring Boot version");
        assertTrue(analysis.contains("3.2.x"), 
            "Analysis should reference target Spring Boot version");
    }

    @Test
    @DisplayName("Should document Java version upgrade requirement")
    public void testJavaVersionUpgradeDocumented() throws IOException {
        String rootPom = Files.readString(Paths.get(ROOT_POM));
        String analysis = Files.readString(Paths.get(ANALYSIS_FILE));
        
        assertTrue(rootPom.contains("<java.version>1.8</java.version>"), 
            "Root POM should show current Java 1.8");
        assertTrue(analysis.contains("Java 1.8"), 
            "Analysis should document current Java version");
        assertTrue(analysis.contains("Java 17+"), 
            "Analysis should document required Java 17+");
    }

    @Test
    @DisplayName("Should document all Spring Boot starters from monolith module")
    public void testSpringBootStartersDocumented() throws IOException {
        String monolithPom = Files.readString(Paths.get(MONOLITH_POM));
        String analysis = Files.readString(Paths.get(ANALYSIS_FILE));
        
        // Verify all starters are documented
        List<String> expectedStarters = List.of(
            "spring-boot-starter-data-jpa",
            "spring-boot-starter-activemq", 
            "spring-boot-starter-web",
            "spring-boot-starter-thymeleaf",
            "spring-boot-starter-test"
        );
        
        for (String starter : expectedStarters) {
            assertTrue(monolithPom.contains(starter), 
                "Monolith POM should contain " + starter);
            assertTrue(analysis.contains(starter), 
                "Analysis should document " + starter);
        }
    }

    @Test
    @DisplayName("Should document WebJars dependencies with versions")
    public void testWebJarsDependenciesDocumented() throws IOException {
        String rootPom = Files.readString(Paths.get(ROOT_POM));
        String analysis = Files.readString(Paths.get(ANALYSIS_FILE));
        
        // Check WebJars versions are documented
        assertTrue(rootPom.contains("<webjars-bootstrap.version>3.3.6</webjars-bootstrap.version>"), 
            "Root POM should contain Bootstrap version");
        assertTrue(rootPom.contains("<webjars-jquery.version>2.2.4</webjars-jquery.version>"), 
            "Root POM should contain jQuery version");
        assertTrue(rootPom.contains("<webjars-jquery-ui.version>1.11.4</webjars-jquery-ui.version>"), 
            "Root POM should contain jQuery UI version");
        
        assertTrue(analysis.contains("Bootstrap**: 3.3.6"), 
            "Analysis should document Bootstrap version");
        assertTrue(analysis.contains("jQuery**: 2.2.4"), 
            "Analysis should document jQuery version");
        assertTrue(analysis.contains("jQuery UI**: 1.11.4"), 
            "Analysis should document jQuery UI version");
    }

    @Test
    @DisplayName("Should document ActiveMQ dependencies")
    public void testActiveMQDependenciesDocumented() throws IOException {
        String activemqPom = Files.readString(Paths.get(ACTIVEMQ_POM));
        String analysis = Files.readString(Paths.get(ANALYSIS_FILE));
        
        assertTrue(activemqPom.contains("activemq-broker"), 
            "ActiveMQ POM should contain activemq-broker");
        assertTrue(activemqPom.contains("activemq-pool"), 
            "ActiveMQ POM should contain activemq-pool");
        assertTrue(activemqPom.contains("hawtio-springboot"), 
            "ActiveMQ POM should contain hawtio-springboot");
        
        assertTrue(analysis.contains("activemq-broker"), 
            "Analysis should document activemq-broker");
        assertTrue(analysis.contains("hawtio-springboot:2.8.0"), 
            "Analysis should document hawtio version");
    }

    @Test
    @DisplayName("Should identify javax to jakarta migration requirement")
    public void testJavaxToJakartaMigrationDocumented() throws IOException {
        String analysis = Files.readString(Paths.get(ANALYSIS_FILE));
        
        assertTrue(analysis.contains("javax"), 
            "Analysis should mention javax packages");
        assertTrue(analysis.contains("jakarta"), 
            "Analysis should mention jakarta packages");
        assertTrue(analysis.contains("javax.* packages → jakarta.*"), 
            "Analysis should document namespace migration");
    }

    @Test
    @DisplayName("Should document breaking changes and migration strategy")
    public void testBreakingChangesAndMigrationStrategy() throws IOException {
        String analysis = Files.readString(Paths.get(ANALYSIS_FILE));
        
        assertTrue(analysis.contains("Breaking Changes"), 
            "Analysis should document breaking changes");
        assertTrue(analysis.contains("Migration Strategy"), 
            "Analysis should provide migration strategy");
        assertTrue(analysis.contains("Phase 1"), 
            "Analysis should have phased migration approach");
        assertTrue(analysis.contains("Estimated Migration Effort"), 
            "Analysis should estimate migration effort");
    }

    @Test
    @DisplayName("Should identify high-risk dependencies")
    public void testHighRiskDependenciesIdentified() throws IOException {
        String analysis = Files.readString(Paths.get(ANALYSIS_FILE));
        
        assertTrue(analysis.contains("High-Risk Dependencies"), 
            "Analysis should identify high-risk dependencies");
        assertTrue(analysis.contains("WRO4J"), 
            "Analysis should identify WRO4J as high-risk");
        assertTrue(analysis.contains("Risk Level"), 
            "Analysis should categorize risk levels");
    }

    @Test
    @DisplayName("Should document security considerations")
    public void testSecurityConsiderationsDocumented() throws IOException {
        String analysis = Files.readString(Paths.get(ANALYSIS_FILE));
        
        assertTrue(analysis.contains("Security Considerations"), 
            "Analysis should include security considerations");
        assertTrue(analysis.contains("Current Vulnerabilities"), 
            "Analysis should document current vulnerabilities");
        assertTrue(analysis.contains("End-of-life"), 
            "Analysis should mention end-of-life status");
    }
}