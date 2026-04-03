#!/usr/bin/env python3
"""
Comprehensive test suite to verify dependency audit acceptance criteria
"""

import os
import re
import json
from pathlib import Path

class DependencyAuditValidator:
    def __init__(self):
        self.test_output = []
        self.criteria_met = []
        self.criteria_failed = []
    
    def log(self, message):
        """Log test output"""
        self.test_output.append(message)
        print(message)
    
    def test_dependency_inventory_complete(self):
        """Test: Complete inventory of all current dependencies documented"""
        self.log("Testing: Complete dependency inventory...")
        
        try:
            # Check if inventory file exists
            inventory_file = Path("DEPENDENCY_INVENTORY.md")
            if not inventory_file.exists():
                self.log("❌ DEPENDENCY_INVENTORY.md not found")
                return False
            
            content = inventory_file.read_text()
            
            # Check for required sections
            required_sections = [
                "Root Project Configuration",
                "Monolith Module Dependencies", 
                "ActiveMQ Module Dependencies",
                "Security Vulnerabilities",
                "Transitive Dependencies Analysis"
            ]
            
            missing_sections = []
            for section in required_sections:
                if section not in content:
                    missing_sections.append(section)
            
            if missing_sections:
                self.log(f"❌ Missing sections: {missing_sections}")
                return False
            
            # Check for specific dependencies that should be documented
            required_dependencies = [
                "spring-boot-starter-data-jpa",
                "spring-boot-starter-web", 
                "spring-boot-starter-thymeleaf",
                "spring-boot-starter-activemq",
                "jquery",
                "bootstrap",
                "activemq-broker",
                "hawtio-springboot"
            ]
            
            missing_deps = []
            for dep in required_dependencies:
                if dep not in content:
                    missing_deps.append(dep)
            
            if missing_deps:
                self.log(f"❌ Missing dependencies: {missing_deps}")
                return False
            
            # Check for version information
            if "2.2.1.RELEASE" not in content:
                self.log("❌ Current Spring Boot version not documented")
                return False
            
            # Check for security vulnerability documentation
            if "CVE" not in content:
                self.log("❌ Security vulnerabilities not documented")
                return False
            
            self.log("✅ Complete dependency inventory documented")
            return True
            
        except Exception as e:
            self.log(f"❌ Error testing dependency inventory: {e}")
            return False
    
    def test_spring_boot_3_compatibility_matrix(self):
        """Test: Spring Boot 3.x compatibility matrix created for all dependencies"""
        self.log("Testing: Spring Boot 3.x compatibility matrix...")
        
        try:
            # Check if compatibility matrix file exists
            matrix_file = Path("SPRING_BOOT_3_COMPATIBILITY_MATRIX.md")
            if not matrix_file.exists():
                self.log("❌ SPRING_BOOT_3_COMPATIBILITY_MATRIX.md not found")
                return False
            
            content = matrix_file.read_text()
            
            # Check for required sections
            required_sections = [
                "Spring Boot 3.x Requirements",
                "Core Dependencies Compatibility",
                "Breaking Changes Analysis",
                "Jakarta EE Migration",
                "Migration Complexity Assessment"
            ]
            
            missing_sections = []
            for section in required_sections:
                if section not in content:
                    missing_sections.append(section)
            
            if missing_sections:
                self.log(f"❌ Missing sections: {missing_sections}")
                return False
            
            # Check for Java 17 requirement
            if "Java 17" not in content:
                self.log("❌ Java 17 requirement not documented")
                return False
            
            # Check for Jakarta EE migration documentation
            if "jakarta" not in content.lower():
                self.log("❌ Jakarta EE migration not documented")
                return False
            
            # Check for compatibility status indicators
            compatibility_indicators = ["✅ Compatible", "⚠️", "❌"]
            has_indicators = any(indicator in content for indicator in compatibility_indicators)
            if not has_indicators:
                self.log("❌ Compatibility status indicators not found")
                return False
            
            # Check for specific Spring Boot 3.x versions
            if "3.2" not in content and "3.0" not in content:
                self.log("❌ Spring Boot 3.x versions not specified")
                return False
            
            self.log("✅ Spring Boot 3.x compatibility matrix created")
            return True
            
        except Exception as e:
            self.log(f"❌ Error testing compatibility matrix: {e}")
            return False
    
    def test_upgrade_sequence_plan(self):
        """Test: Upgrade sequence plan documented with intermediate versions"""
        self.log("Testing: Upgrade sequence plan with intermediate versions...")
        
        try:
            # Check if upgrade plan file exists
            plan_file = Path("UPGRADE_SEQUENCE_PLAN.md")
            if not plan_file.exists():
                self.log("❌ UPGRADE_SEQUENCE_PLAN.md not found")
                return False
            
            content = plan_file.read_text()
            
            # Check for required sections
            required_sections = [
                "Migration Strategy",
                "Phase 1",
                "Phase 2", 
                "Phase 3",
                "Phase 4",
                "Phase 5",
                "Risk Management",
                "Timeline Summary"
            ]
            
            missing_sections = []
            for section in required_sections:
                if section not in content:
                    missing_sections.append(section)
            
            if missing_sections:
                self.log(f"❌ Missing sections: {missing_sections}")
                return False
            
            # Check for intermediate Spring Boot version (2.7.x)
            if "2.7" not in content:
                self.log("❌ Intermediate Spring Boot 2.7.x version not documented")
                return False
            
            # Check for incremental approach
            if "incremental" not in content.lower():
                self.log("❌ Incremental upgrade approach not documented")
                return False
            
            # Check for timeline information
            if "weeks" not in content.lower() and "months" not in content.lower():
                self.log("❌ Timeline information not provided")
                return False
            
            # Check for risk assessment
            if "risk" not in content.lower():
                self.log("❌ Risk assessment not documented")
                return False
            
            # Check for rollback strategy
            if "rollback" not in content.lower():
                self.log("❌ Rollback strategy not documented")
                return False
            
            # Check for specific phase objectives
            phase_keywords = ["Java 17", "Spring Boot 2.7", "Jakarta EE", "Spring Boot 3"]
            missing_phases = []
            for keyword in phase_keywords:
                if keyword not in content:
                    missing_phases.append(keyword)
            
            if missing_phases:
                self.log(f"❌ Missing phase content: {missing_phases}")
                return False
            
            self.log("✅ Upgrade sequence plan with intermediate versions documented")
            return True
            
        except Exception as e:
            self.log(f"❌ Error testing upgrade sequence plan: {e}")
            return False
    
    def test_documentation_quality(self):
        """Test: Documentation quality and completeness"""
        self.log("Testing: Documentation quality...")
        
        try:
            # Check if summary file exists
            summary_file = Path("DEPENDENCY_AUDIT_SUMMARY.md")
            if not summary_file.exists():
                self.log("❌ DEPENDENCY_AUDIT_SUMMARY.md not found")
                return False
            
            content = summary_file.read_text()
            
            # Check for executive summary elements
            required_elements = [
                "Executive Overview",
                "Critical Issues",
                "Recommended Approach",
                "Risk Assessment",
                "Success Metrics"
            ]
            
            missing_elements = []
            for element in required_elements:
                if element not in content:
                    missing_elements.append(element)
            
            if missing_elements:
                self.log(f"❌ Missing summary elements: {missing_elements}")
                return False
            
            self.log("✅ Documentation quality verified")
            return True
            
        except Exception as e:
            self.log(f"❌ Error testing documentation quality: {e}")
            return False
    
    def run_all_tests(self):
        """Run all validation tests"""
        self.log("=== Dependency Audit Validation Tests ===")
        
        # Test 1: Complete dependency inventory
        test1_passed = self.test_dependency_inventory_complete()
        if test1_passed:
            self.criteria_met.append("Complete inventory of all current dependencies documented")
        else:
            self.criteria_failed.append("Complete inventory of all current dependencies documented")
        
        # Test 2: Spring Boot 3.x compatibility matrix
        test2_passed = self.test_spring_boot_3_compatibility_matrix()
        if test2_passed:
            self.criteria_met.append("Spring Boot 3.x compatibility matrix created for all dependencies")
        else:
            self.criteria_failed.append("Spring Boot 3.x compatibility matrix created for all dependencies")
        
        # Test 3: Upgrade sequence plan
        test3_passed = self.test_upgrade_sequence_plan()
        if test3_passed:
            self.criteria_met.append("Upgrade sequence plan documented with intermediate versions")
        else:
            self.criteria_failed.append("Upgrade sequence plan documented with intermediate versions")
        
        # Test 4: Documentation quality
        test4_passed = self.test_documentation_quality()
        if test4_passed:
            self.criteria_met.append("High-quality documentation with executive summary")
        else:
            self.criteria_failed.append("High-quality documentation with executive summary")
        
        # Overall result
        all_tests_passed = test1_passed and test2_passed and test3_passed and test4_passed
        
        # Summary
        self.log("\n=== Test Summary ===")
        self.log(f"Tests Passed: {all_tests_passed}")
        self.log(f"Criteria Met: {len(self.criteria_met)}")
        self.log(f"Criteria Failed: {len(self.criteria_failed)}")
        
        if all_tests_passed:
            self.log("🎉 All acceptance criteria verified successfully!")
        else:
            self.log("⚠️  Some acceptance criteria not met")
        
        # Return results
        results = {
            "tests_passed": all_tests_passed,
            "test_output": "\n".join(self.test_output),
            "criteria_met": self.criteria_met,
            "criteria_failed": self.criteria_failed
        }
        
        return results

if __name__ == "__main__":
    validator = DependencyAuditValidator()
    results = validator.run_all_tests()
    
    # Output JSON results
    print("\n" + "="*50)
    print("JSON RESULTS:")
    print(json.dumps(results, indent=2))