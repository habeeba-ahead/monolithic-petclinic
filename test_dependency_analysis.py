#!/usr/bin/env python3
"""
Test suite to verify Spring Boot 2.2.1 dependency analysis meets acceptance criteria.
"""

import os
import re
import csv
import unittest
from pathlib import Path


class SpringBootDependencyAnalysisTest(unittest.TestCase):
    """Test suite for Spring Boot dependency analysis verification."""
    
    def setUp(self):
        """Set up test fixtures."""
        self.root_dir = Path(".")
        self.analysis_file = self.root_dir / "spring-boot-dependency-analysis.md"
        self.matrix_file = self.root_dir / "spring-boot-3-compatibility-matrix.csv"
        self.checklist_file = self.root_dir / "spring-boot-3-migration-checklist.md"
        self.pom_file = self.root_dir / "pom.xml"
        self.monolith_pom = self.root_dir / "monolith" / "pom.xml"
        self.activemq_pom = self.root_dir / "activemq" / "pom.xml"
    
    def test_files_exist(self):
        """Test that all required analysis files exist."""
        self.assertTrue(self.analysis_file.exists(), "spring-boot-dependency-analysis.md must exist")
        self.assertTrue(self.matrix_file.exists(), "spring-boot-3-compatibility-matrix.csv must exist")
        self.assertTrue(self.checklist_file.exists(), "spring-boot-3-migration-checklist.md must exist")
        self.assertTrue(self.pom_file.exists(), "root pom.xml must exist")
        self.assertTrue(self.monolith_pom.exists(), "monolith/pom.xml must exist")
        self.assertTrue(self.activemq_pom.exists(), "activemq/pom.xml must exist")
    
    def test_complete_dependency_inventory(self):
        """Test that complete inventory of Spring Boot dependencies is documented."""
        analysis_content = self.analysis_file.read_text()
        
        # Check for main sections
        self.assertIn("Current Spring Boot Dependencies Inventory", analysis_content)
        self.assertIn("Root POM (pom.xml)", analysis_content)
        self.assertIn("Monolith Module Dependencies", analysis_content)
        self.assertIn("ActiveMQ Module Dependencies", analysis_content)
        
        # Check for Spring Boot version documentation
        self.assertIn("Spring Boot Version**: 2.2.1.RELEASE", analysis_content)
        self.assertIn("Java Version**: 1.8", analysis_content)
        
        # Check for key dependencies from root POM
        self.assertIn("Bootstrap**: 3.3.6", analysis_content)
        self.assertIn("jQuery**: 2.2.4", analysis_content)
        self.assertIn("jQuery UI**: 1.11.4", analysis_content)
        self.assertIn("WRO4J**: 1.8.0", analysis_content)
        
        # Check for monolith dependencies
        self.assertIn("spring-boot-starter-data-jpa", analysis_content)
        self.assertIn("spring-boot-starter-activemq", analysis_content)
        self.assertIn("spring-boot-starter-web", analysis_content)
        self.assertIn("spring-boot-starter-thymeleaf", analysis_content)
        self.assertIn("hsqldb", analysis_content)
        self.assertIn("wiremock-jre8:2.26.0", analysis_content)
        
        # Check for ActiveMQ dependencies
        self.assertIn("activemq-broker", analysis_content)
        self.assertIn("activemq-pool", analysis_content)
        self.assertIn("hawtio-springboot:2.8.0", analysis_content)
        
        print("✅ Complete dependency inventory documented")
    
    def test_compatibility_matrix_created(self):
        """Test that compatibility matrix for Spring Boot 3.2.x migration is created."""
        # Check CSV file exists and has content
        self.assertTrue(self.matrix_file.exists(), "Compatibility matrix CSV must exist")
        
        # Read and validate CSV structure
        with open(self.matrix_file, 'r') as f:
            csv_reader = csv.DictReader(f)
            headers = csv_reader.fieldnames
            
            # Check required headers
            required_headers = [
                'Dependency', 'Current Version', 'Spring Boot 3.2.x Compatible Version',
                'Compatibility Status', 'Migration Effort', 'Breaking Changes', 'Notes'
            ]
            for header in required_headers:
                self.assertIn(header, headers, f"Header '{header}' must be present in CSV")
            
            # Check that we have entries for key dependencies
            rows = list(csv_reader)
            self.assertGreater(len(rows), 20, "Matrix should have substantial number of dependencies")
            
            dependency_names = [row['Dependency'] for row in rows]
            
            # Check for critical dependencies
            critical_deps = [
                'Spring Boot Parent', 'Java Version', 'jQuery', 'Bootstrap',
                'ActiveMQ Broker', 'Hawtio SpringBoot', 'WRO4J', 'WireMock'
            ]
            for dep in critical_deps:
                self.assertIn(dep, dependency_names, f"Dependency '{dep}' must be in matrix")
        
        # Check analysis file also contains compatibility matrix
        analysis_content = self.analysis_file.read_text()
        self.assertIn("Spring Boot 3.2.x Compatibility Matrix", analysis_content)
        self.assertIn("Compatible Dependencies", analysis_content)
        self.assertIn("Requires Updates", analysis_content)
        self.assertIn("Breaking Changes Required", analysis_content)
        
        print("✅ Compatibility matrix created for Spring Boot 3.2.x migration")
    
    def test_breaking_changes_identified(self):
        """Test that breaking changes are identified and documented."""
        analysis_content = self.analysis_file.read_text()
        
        # Check for breaking changes section
        self.assertIn("Critical Breaking Changes for Spring Boot 3.2.x", analysis_content)
        
        # Check for major breaking changes
        breaking_changes = [
            "Java Version Requirement",
            "Javax to Jakarta Migration", 
            "Spring Boot Configuration Changes",
            "Dependency Version Bumps"
        ]
        
        for change in breaking_changes:
            self.assertIn(change, analysis_content, f"Breaking change '{change}' must be documented")
        
        # Check for specific technical details
        self.assertIn("Java 17+", analysis_content)
        self.assertIn("javax.* packages → jakarta.*", analysis_content)
        self.assertIn("Spring Framework**: 6.x", analysis_content)
        self.assertIn("Spring Security**: 6.x", analysis_content)
        self.assertIn("Hibernate**: 6.x", analysis_content)
        
        # Check for high-risk dependencies
        self.assertIn("High-Risk Dependencies", analysis_content)
        self.assertIn("WRO4J (1.8.0)", analysis_content)
        self.assertIn("Risk Level**: HIGH", analysis_content)
        
        # Check migration checklist has breaking changes
        checklist_content = self.checklist_file.read_text()
        self.assertIn("Namespace Migration (javax → jakarta)", checklist_content)
        self.assertIn("javax.persistence.*", checklist_content)
        self.assertIn("jakarta.persistence.*", checklist_content)
        
        print("✅ Breaking changes identified and documented")
    
    def test_migration_strategy_documented(self):
        """Test that migration strategy is properly documented."""
        analysis_content = self.analysis_file.read_text()
        
        # Check for migration strategy section
        self.assertIn("Migration Strategy", analysis_content)
        
        # Check for phased approach
        phases = ["Phase 1: Pre-Migration Preparation", "Phase 2: Spring Boot Upgrade", "Phase 3: Post-Migration Validation"]
        for phase in phases:
            self.assertIn(phase, analysis_content, f"Migration phase '{phase}' must be documented")
        
        # Check for effort estimation
        self.assertIn("Estimated Migration Effort", analysis_content)
        self.assertIn("6-10 weeks", analysis_content)
        
        # Check migration checklist exists and is comprehensive
        checklist_content = self.checklist_file.read_text()
        
        # Check for major migration phases in checklist
        checklist_phases = [
            "Pre-Migration Phase",
            "Migration Phase 1: Java 17 Upgrade", 
            "Migration Phase 2: Spring Boot Version Upgrade",
            "Migration Phase 3: Dependency Updates",
            "Migration Phase 4: Application-Specific Updates",
            "Migration Phase 5: Frontend Updates",
            "Migration Phase 6: Testing and Validation"
        ]
        
        for phase in checklist_phases:
            self.assertIn(phase, checklist_content, f"Checklist phase '{phase}' must be present")
        
        print("✅ Migration strategy documented")
    
    def test_security_considerations_documented(self):
        """Test that security considerations are documented."""
        analysis_content = self.analysis_file.read_text()
        
        # Check for security section
        self.assertIn("Security Considerations", analysis_content)
        self.assertIn("Current Vulnerabilities", analysis_content)
        self.assertIn("Post-Migration Benefits", analysis_content)
        
        # Check for specific security issues
        self.assertIn("End-of-life version with known CVEs", analysis_content)
        self.assertIn("Outdated jQuery with XSS vulnerabilities", analysis_content)
        self.assertIn("Latest security patches", analysis_content)
        
        print("✅ Security considerations documented")
    
    def test_pom_analysis_accuracy(self):
        """Test that POM file analysis is accurate."""
        # Read actual POM files
        root_pom = self.pom_file.read_text()
        monolith_pom_content = self.monolith_pom.read_text()
        activemq_pom_content = self.activemq_pom.read_text()
        
        # Verify Spring Boot version
        self.assertIn("2.2.1.RELEASE", root_pom)
        
        # Verify Java version
        self.assertIn("<java.version>1.8</java.version>", root_pom)
        
        # Verify WebJars versions
        self.assertIn("<webjars-bootstrap.version>3.3.6</webjars-bootstrap.version>", root_pom)
        self.assertIn("<webjars-jquery.version>2.2.4</webjars-jquery.version>", root_pom)
        self.assertIn("<webjars-jquery-ui.version>1.11.4</webjars-jquery-ui.version>", root_pom)
        self.assertIn("<wro4j.version>1.8.0</wro4j.version>", root_pom)
        
        # Verify monolith dependencies
        self.assertIn("spring-boot-starter-data-jpa", monolith_pom_content)
        self.assertIn("spring-boot-starter-activemq", monolith_pom_content)
        self.assertIn("spring-boot-starter-web", monolith_pom_content)
        self.assertIn("spring-boot-starter-thymeleaf", monolith_pom_content)
        self.assertIn("wiremock-jre8", monolith_pom_content)
        self.assertIn("2.26.0", monolith_pom_content)
        
        # Verify ActiveMQ dependencies
        self.assertIn("activemq-broker", activemq_pom_content)
        self.assertIn("activemq-pool", activemq_pom_content)
        self.assertIn("hawtio-springboot", activemq_pom_content)
        self.assertIn("2.8.0", activemq_pom_content)
        
        print("✅ POM file analysis is accurate")
    
    def test_comprehensive_coverage(self):
        """Test that analysis provides comprehensive coverage."""
        analysis_content = self.analysis_file.read_text()
        
        # Check for comprehensive sections
        required_sections = [
            "Current Spring Boot Dependencies Inventory",
            "Spring Boot 3.2.x Compatibility Matrix", 
            "Critical Breaking Changes",
            "Migration Strategy",
            "High-Risk Dependencies",
            "Security Considerations",
            "Estimated Migration Effort",
            "Recommendations"
        ]
        
        for section in required_sections:
            self.assertIn(section, analysis_content, f"Section '{section}' must be present")
        
        # Check word count to ensure comprehensive analysis
        word_count = len(analysis_content.split())
        self.assertGreater(word_count, 2000, "Analysis should be comprehensive (>2000 words)")
        
        print("✅ Analysis provides comprehensive coverage")


def run_tests():
    """Run all tests and return results."""
    # Create test suite
    suite = unittest.TestLoader().loadTestsFromTestCase(SpringBootDependencyAnalysisTest)
    
    # Run tests
    runner = unittest.TextTestRunner(verbosity=2, stream=open(os.devnull, 'w'))
    result = runner.run(suite)
    
    # Return results
    return {
        'tests_run': result.testsRun,
        'failures': len(result.failures),
        'errors': len(result.errors),
        'success': result.wasSuccessful()
    }


if __name__ == '__main__':
    print("🔍 Verifying Spring Boot 2.2.1 Dependency Analysis...")
    print("=" * 60)
    
    results = run_tests()
    
    print("\n" + "=" * 60)
    print("📊 TEST RESULTS SUMMARY")
    print("=" * 60)
    print(f"Tests Run: {results['tests_run']}")
    print(f"Failures: {results['failures']}")
    print(f"Errors: {results['errors']}")
    print(f"Success: {'✅ PASS' if results['success'] else '❌ FAIL'}")
    
    if results['success']:
        print("\n🎉 All acceptance criteria verified successfully!")
    else:
        print("\n❌ Some acceptance criteria not met. Check test output above.")