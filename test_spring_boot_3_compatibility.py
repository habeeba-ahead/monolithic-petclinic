#!/usr/bin/env python3
"""
Test suite to verify Spring Boot 3.x compatibility assessment meets all acceptance criteria.
"""

import os
import re
import json
from pathlib import Path

class SpringBoot3CompatibilityTest:
    def __init__(self):
        self.test_results = {
            "tests_passed": True,
            "test_output": [],
            "criteria_met": [],
            "criteria_failed": []
        }
        
    def log_result(self, test_name, passed, message):
        """Log test result"""
        status = "PASS" if passed else "FAIL"
        self.test_results["test_output"].append(f"{status}: {test_name} - {message}")
        if not passed:
            self.test_results["tests_passed"] = False
            
    def test_compatibility_assessment_document(self):
        """Test that compatibility assessment document exists and contains required content"""
        file_path = "SPRING_BOOT_3_COMPATIBILITY_ASSESSMENT.md"
        
        if not os.path.exists(file_path):
            self.log_result("Compatibility Assessment Document", False, "Document not found")
            return False
            
        with open(file_path, 'r') as f:
            content = f.read()
            
        # Check for required sections
        required_sections = [
            "Executive Summary",
            "Current State Analysis", 
            "Breaking Changes",
            "Java Version Requirement",
            "Jakarta EE Migration",
            "Risk Assessment",
            "Migration Strategy"
        ]
        
        missing_sections = []
        for section in required_sections:
            if section.lower() not in content.lower():
                missing_sections.append(section)
                
        if missing_sections:
            self.log_result("Compatibility Assessment Content", False, 
                          f"Missing sections: {', '.join(missing_sections)}")
            return False
            
        # Check for breaking changes documentation
        breaking_changes = [
            "Java 17",
            "jakarta",
            "javax",
            "Hibernate 6",
            "CVE"
        ]
        
        missing_changes = []
        for change in breaking_changes:
            if change.lower() not in content.lower():
                missing_changes.append(change)
                
        if missing_changes:
            self.log_result("Breaking Changes Documentation", False,
                          f"Missing breaking changes: {', '.join(missing_changes)}")
            return False
            
        # Check for specific file references (javax to jakarta migration)
        if "monolith/src/main/java" not in content:
            self.log_result("File References", False, "Missing specific file references for migration")
            return False
            
        self.log_result("Compatibility Assessment Document", True, 
                       "Document exists with all required sections and breaking changes")
        self.test_results["criteria_met"].append("Compatibility assessment document created listing all breaking changes")
        return True
        
    def test_migration_checklist_document(self):
        """Test that migration checklist exists and contains actionable items"""
        file_path = "JAVAX_TO_JAKARTA_MIGRATION_CHECKLIST.md"
        
        if not os.path.exists(file_path):
            self.log_result("Migration Checklist Document", False, "Document not found")
            return False
            
        with open(file_path, 'r') as f:
            content = f.read()
            
        # Check for checklist format (checkboxes)
        checkbox_pattern = r'- \[ \]'
        checkboxes = re.findall(checkbox_pattern, content)
        
        if len(checkboxes) < 20:  # Should have many actionable items
            self.log_result("Migration Checklist Format", False, 
                          f"Only {len(checkboxes)} checklist items found, expected 20+")
            return False
            
        # Check for required migration sections
        required_sections = [
            "Pre-Migration Setup",
            "Package Import Changes", 
            "JPA/Persistence Imports",
            "Validation Imports",
            "Dependency Updates",
            "Testing and Validation",
            "Rollback Plan"
        ]
        
        missing_sections = []
        for section in required_sections:
            if section.lower() not in content.lower():
                missing_sections.append(section)
                
        if missing_sections:
            self.log_result("Migration Checklist Sections", False,
                          f"Missing sections: {', '.join(missing_sections)}")
            return False
            
        # Check for specific javax to jakarta mappings
        javax_mappings = [
            "javax.persistence",
            "jakarta.persistence", 
            "javax.validation",
            "jakarta.validation"
        ]
        
        missing_mappings = []
        for mapping in javax_mappings:
            if mapping not in content:
                missing_mappings.append(mapping)
                
        if missing_mappings:
            self.log_result("Package Mappings", False,
                          f"Missing package mappings: {', '.join(missing_mappings)}")
            return False
            
        # Check for automation scripts
        if "find . -name" not in content or "sed -i" not in content:
            self.log_result("Automation Scripts", False, "Missing automation scripts for migration")
            return False
            
        self.log_result("Migration Checklist Document", True,
                       f"Document exists with {len(checkboxes)} actionable items and automation scripts")
        self.test_results["criteria_met"].append("Migration checklist created for javax to jakarta package changes")
        return True
        
    def test_dependency_compatibility_matrix(self):
        """Test that dependency compatibility matrix exists and is comprehensive"""
        file_path = "DEPENDENCY_COMPATIBILITY_MATRIX.md"
        
        if not os.path.exists(file_path):
            self.log_result("Dependency Compatibility Matrix", False, "Document not found")
            return False
            
        with open(file_path, 'r') as f:
            content = f.read()
            
        # Check for table format (markdown tables)
        table_pattern = r'\|.*\|.*\|.*\|'
        tables = re.findall(table_pattern, content)
        
        if len(tables) < 10:  # Should have multiple dependency tables
            self.log_result("Dependency Tables", False,
                          f"Only {len(tables)} table rows found, expected 10+")
            return False
            
        # Check for required dependency categories
        required_categories = [
            "Core Framework",
            "Data Access", 
            "Web Dependencies",
            "Frontend Dependencies",
            "Messaging Dependencies",
            "Testing Dependencies",
            "Build Tool Dependencies"
        ]
        
        missing_categories = []
        for category in required_categories:
            if category.lower() not in content.lower():
                missing_categories.append(category)
                
        if missing_categories:
            self.log_result("Dependency Categories", False,
                          f"Missing categories: {', '.join(missing_categories)}")
            return False
            
        # Check for compatibility status indicators
        compatibility_indicators = ["✅", "❌", "⚠️"]
        found_indicators = []
        for indicator in compatibility_indicators:
            if indicator in content:
                found_indicators.append(indicator)
                
        if len(found_indicators) < 2:
            self.log_result("Compatibility Indicators", False,
                          "Missing compatibility status indicators (✅❌⚠️)")
            return False
            
        # Check for version information
        version_patterns = [
            r'\d+\.\d+\.\d+',  # Semantic versioning
            r'Current Version',
            r'Recommended Version'
        ]
        
        version_info_found = False
        for pattern in version_patterns:
            if re.search(pattern, content):
                version_info_found = True
                break
                
        if not version_info_found:
            self.log_result("Version Information", False, "Missing version information in matrix")
            return False
            
        # Check for CVE/security information
        if "CVE" not in content:
            self.log_result("Security Information", False, "Missing CVE/security vulnerability information")
            return False
            
        # Check for migration effort estimation
        if "effort" not in content.lower() or "days" not in content.lower():
            self.log_result("Migration Effort", False, "Missing migration effort estimation")
            return False
            
        self.log_result("Dependency Compatibility Matrix", True,
                       f"Comprehensive matrix with {len(tables)} entries, compatibility indicators, and effort estimates")
        self.test_results["criteria_met"].append("Dependency compatibility matrix created for all current dependencies")
        return True
        
    def test_document_quality_and_completeness(self):
        """Test overall quality and completeness of all documents"""
        documents = [
            "SPRING_BOOT_3_COMPATIBILITY_ASSESSMENT.md",
            "JAVAX_TO_JAKARTA_MIGRATION_CHECKLIST.md", 
            "DEPENDENCY_COMPATIBILITY_MATRIX.md"
        ]
        
        total_size = 0
        for doc in documents:
            if os.path.exists(doc):
                total_size += os.path.getsize(doc)
                
        # Documents should be substantial (combined > 20KB indicates thorough documentation)
        if total_size < 20000:
            self.log_result("Document Completeness", False,
                          f"Combined document size {total_size} bytes is too small for comprehensive assessment")
            return False
            
        # Check for cross-references between documents
        assessment_content = ""
        checklist_content = ""
        matrix_content = ""
        
        if os.path.exists(documents[0]):
            with open(documents[0], 'r') as f:
                assessment_content = f.read()
                
        if os.path.exists(documents[1]):
            with open(documents[1], 'r') as f:
                checklist_content = f.read()
                
        if os.path.exists(documents[2]):
            with open(documents[2], 'r') as f:
                matrix_content = f.read()
                
        # Check for consistency in version numbers and recommendations
        spring_boot_versions = []
        java_versions = []
        
        for content in [assessment_content, checklist_content, matrix_content]:
            # Find Spring Boot versions
            sb_matches = re.findall(r'Spring Boot.*?(\d+\.\d+)', content)
            spring_boot_versions.extend(sb_matches)
            
            # Find Java versions  
            java_matches = re.findall(r'Java (\d+)', content)
            java_versions.extend(java_matches)
            
        # Check for consistency
        if len(set(spring_boot_versions)) > 2:  # Allow some variation but not too much
            self.log_result("Version Consistency", False,
                          f"Inconsistent Spring Boot versions across documents: {set(spring_boot_versions)}")
            return False
            
        if len(set(java_versions)) > 2:  # Should consistently recommend Java 17/21
            self.log_result("Java Version Consistency", False,
                          f"Inconsistent Java versions across documents: {set(java_versions)}")
            return False
            
        self.log_result("Document Quality", True,
                       f"All documents are comprehensive ({total_size} bytes total) with consistent recommendations")
        return True
        
    def test_actionability_and_implementation_readiness(self):
        """Test that documents provide actionable guidance for implementation"""
        
        # Check migration checklist for specific file paths
        checklist_path = "JAVAX_TO_JAKARTA_MIGRATION_CHECKLIST.md"
        if os.path.exists(checklist_path):
            with open(checklist_path, 'r') as f:
                checklist_content = f.read()
                
            # Should contain specific file paths for migration
            file_path_pattern = r'monolith/src/main/java/.*\.java'
            file_paths = re.findall(file_path_pattern, checklist_content)
            
            if len(file_paths) < 5:
                self.log_result("Specific File Paths", False,
                              f"Only {len(file_paths)} specific file paths found, need more detailed guidance")
                return False
                
            # Should contain before/after code examples
            if "// BEFORE" not in checklist_content or "// AFTER" not in checklist_content:
                self.log_result("Code Examples", False, "Missing before/after code examples")
                return False
                
        # Check compatibility matrix for specific version recommendations
        matrix_path = "DEPENDENCY_COMPATIBILITY_MATRIX.md"
        if os.path.exists(matrix_path):
            with open(matrix_path, 'r') as f:
                matrix_content = f.read()
                
            # Should contain specific version numbers
            version_pattern = r'\d+\.\d+\.\d+'
            versions = re.findall(version_pattern, matrix_content)
            
            if len(versions) < 10:
                self.log_result("Version Specifications", False,
                              f"Only {len(versions)} specific versions found, need more detailed recommendations")
                return False
                
        self.log_result("Implementation Readiness", True,
                       "Documents provide specific, actionable guidance with file paths and version numbers")
        return True
        
    def run_all_tests(self):
        """Run all tests and return results"""
        print("Running Spring Boot 3.x Compatibility Assessment Tests...")
        print("=" * 60)
        
        # Run individual tests
        self.test_compatibility_assessment_document()
        self.test_migration_checklist_document() 
        self.test_dependency_compatibility_matrix()
        self.test_document_quality_and_completeness()
        self.test_actionability_and_implementation_readiness()
        
        # Print results
        print("\nTest Results:")
        print("-" * 40)
        for output in self.test_results["test_output"]:
            print(output)
            
        print(f"\nOverall Result: {'PASS' if self.test_results['tests_passed'] else 'FAIL'}")
        print(f"Criteria Met: {len(self.test_results['criteria_met'])}")
        print(f"Criteria Failed: {len(self.test_results['criteria_failed'])}")
        
        return self.test_results

if __name__ == "__main__":
    tester = SpringBoot3CompatibilityTest()
    results = tester.run_all_tests()
    
    # Output JSON results
    print("\n" + "=" * 60)
    print("JSON RESULTS:")
    print(json.dumps(results, indent=2))