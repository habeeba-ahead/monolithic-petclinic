#!/usr/bin/env python3
"""
Final comprehensive test suite to verify Spring Boot 3.x compatibility assessment meets all acceptance criteria.
"""

import os
import re
import json
from pathlib import Path

class SpringBoot3CompatibilityFinalTest:
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
            
    def test_acceptance_criteria_1_compatibility_assessment(self):
        """Test Acceptance Criteria 1: Compatibility assessment document created listing all breaking changes"""
        file_path = "SPRING_BOOT_3_COMPATIBILITY_ASSESSMENT.md"
        
        if not os.path.exists(file_path):
            self.log_result("AC1: Compatibility Assessment Document Exists", False, "Document not found")
            self.test_results["criteria_failed"].append("Compatibility assessment document created listing all breaking changes")
            return False
            
        with open(file_path, 'r') as f:
            content = f.read()
            
        # Verify comprehensive breaking changes are documented
        breaking_changes_documented = [
            ("Java Version Requirement", "java.*17" in content.lower()),
            ("Jakarta EE Migration", "jakarta" in content.lower() and "javax" in content.lower()),
            ("Dependency Updates", "hibernate.*6" in content.lower() or "dependency" in content.lower()),
            ("Configuration Changes", "configuration" in content.lower() or "properties" in content.lower()),
            ("Security Vulnerabilities", "cve" in content.lower()),
            ("Build Tool Updates", "maven" in content.lower()),
            ("Risk Assessment", "risk" in content.lower()),
            ("Migration Strategy", "migration" in content.lower() and "strategy" in content.lower())
        ]
        
        missing_changes = []
        for change_name, found in breaking_changes_documented:
            if not found:
                missing_changes.append(change_name)
                
        if missing_changes:
            self.log_result("AC1: Breaking Changes Documentation", False,
                          f"Missing breaking changes: {', '.join(missing_changes)}")
            self.test_results["criteria_failed"].append("Compatibility assessment document created listing all breaking changes")
            return False
            
        # Verify specific file references for javax to jakarta migration
        if not re.search(r'monolith/src/main/java.*\.java', content):
            self.log_result("AC1: Specific File References", False, "Missing specific file references for migration")
            self.test_results["criteria_failed"].append("Compatibility assessment document created listing all breaking changes")
            return False
            
        # Verify effort estimation
        if not re.search(r'\d+.*week', content.lower()):
            self.log_result("AC1: Effort Estimation", False, "Missing migration effort estimation")
            self.test_results["criteria_failed"].append("Compatibility assessment document created listing all breaking changes")
            return False
            
        self.log_result("AC1: Compatibility Assessment Complete", True, 
                       "Comprehensive assessment with all breaking changes documented")
        self.test_results["criteria_met"].append("Compatibility assessment document created listing all breaking changes")
        return True
        
    def test_acceptance_criteria_2_migration_checklist(self):
        """Test Acceptance Criteria 2: Migration checklist created for javax to jakarta package changes"""
        file_path = "JAVAX_TO_JAKARTA_MIGRATION_CHECKLIST.md"
        
        if not os.path.exists(file_path):
            self.log_result("AC2: Migration Checklist Exists", False, "Document not found")
            self.test_results["criteria_failed"].append("Migration checklist created for javax to jakarta package changes")
            return False
            
        with open(file_path, 'r') as f:
            content = f.read()
            
        # Verify checklist format with actionable items
        checkbox_pattern = r'- \[ \]'
        checkboxes = re.findall(checkbox_pattern, content)
        
        if len(checkboxes) < 30:  # Should have many specific actionable items
            self.log_result("AC2: Actionable Checklist Items", False, 
                          f"Only {len(checkboxes)} checklist items found, expected 30+ for comprehensive migration")
            self.test_results["criteria_failed"].append("Migration checklist created for javax to jakarta package changes")
            return False
            
        # Verify specific javax to jakarta package mappings
        required_mappings = [
            ("javax.persistence", "javax.persistence" in content),
            ("jakarta.persistence", "jakarta.persistence" in content),
            ("javax.validation", "javax.validation" in content),
            ("jakarta.validation", "jakarta.validation" in content)
        ]
        
        missing_mappings = []
        for mapping_name, found in required_mappings:
            if not found:
                missing_mappings.append(mapping_name)
                
        if missing_mappings:
            self.log_result("AC2: Package Mappings", False,
                          f"Missing package mappings: {', '.join(missing_mappings)}")
            self.test_results["criteria_failed"].append("Migration checklist created for javax to jakarta package changes")
            return False
            
        # Verify specific file-by-file migration guidance
        java_file_pattern = r'monolith/src/main/java/.*\.java'
        java_files = re.findall(java_file_pattern, content)
        
        if len(java_files) < 10:  # Should reference specific files that need changes
            self.log_result("AC2: Specific File References", False,
                          f"Only {len(java_files)} specific Java files referenced, need comprehensive file-by-file guidance")
            self.test_results["criteria_failed"].append("Migration checklist created for javax to jakarta package changes")
            return False
            
        # Verify before/after code examples
        if not ("// BEFORE" in content and "// AFTER" in content):
            self.log_result("AC2: Code Examples", False, "Missing before/after code examples for migration")
            self.test_results["criteria_failed"].append("Migration checklist created for javax to jakarta package changes")
            return False
            
        # Verify automation scripts
        if not ("find . -name" in content and "sed -i" in content):
            self.log_result("AC2: Automation Scripts", False, "Missing automation scripts for bulk migration")
            self.test_results["criteria_failed"].append("Migration checklist created for javax to jakarta package changes")
            return False
            
        self.log_result("AC2: Migration Checklist Complete", True,
                       f"Comprehensive checklist with {len(checkboxes)} items, {len(java_files)} file references, and automation scripts")
        self.test_results["criteria_met"].append("Migration checklist created for javax to jakarta package changes")
        return True
        
    def test_acceptance_criteria_3_dependency_matrix(self):
        """Test Acceptance Criteria 3: Dependency compatibility matrix created for all current dependencies"""
        file_path = "DEPENDENCY_COMPATIBILITY_MATRIX.md"
        
        if not os.path.exists(file_path):
            self.log_result("AC3: Dependency Matrix Exists", False, "Document not found")
            self.test_results["criteria_failed"].append("Dependency compatibility matrix created for all current dependencies")
            return False
            
        with open(file_path, 'r') as f:
            content = f.read()
            
        # Verify comprehensive dependency coverage
        required_dependency_categories = [
            ("Core Framework", "spring boot" in content.lower() and "java" in content.lower()),
            ("Data Access", "jpa" in content.lower() and "hibernate" in content.lower()),
            ("Web Dependencies", "web" in content.lower() and "thymeleaf" in content.lower()),
            ("Frontend Dependencies", "bootstrap" in content.lower() and "jquery" in content.lower()),
            ("Messaging", "activemq" in content.lower() or "jms" in content.lower()),
            ("Testing", "junit" in content.lower() and "mockito" in content.lower()),
            ("Build Tools", "maven" in content.lower())
        ]
        
        missing_categories = []
        for category_name, found in required_dependency_categories:
            if not found:
                missing_categories.append(category_name)
                
        if missing_categories:
            self.log_result("AC3: Dependency Categories", False,
                          f"Missing dependency categories: {', '.join(missing_categories)}")
            self.test_results["criteria_failed"].append("Dependency compatibility matrix created for all current dependencies")
            return False
            
        # Verify compatibility status indicators
        compatibility_indicators = ["✅", "❌", "⚠️"]
        found_indicators = sum(1 for indicator in compatibility_indicators if indicator in content)
        
        if found_indicators < 2:
            self.log_result("AC3: Compatibility Status", False,
                          "Missing compatibility status indicators (✅❌⚠️)")
            self.test_results["criteria_failed"].append("Dependency compatibility matrix created for all current dependencies")
            return False
            
        # Verify version information (current and recommended)
        version_columns = ["Current Version", "Recommended Version"]
        missing_version_info = []
        for column in version_columns:
            if column.lower() not in content.lower():
                missing_version_info.append(column)
                
        if missing_version_info:
            self.log_result("AC3: Version Information", False,
                          f"Missing version columns: {', '.join(missing_version_info)}")
            self.test_results["criteria_failed"].append("Dependency compatibility matrix created for all current dependencies")
            return False
            
        # Verify security vulnerability information
        if "cve" not in content.lower():
            self.log_result("AC3: Security Information", False, "Missing CVE/security vulnerability information")
            self.test_results["criteria_failed"].append("Dependency compatibility matrix created for all current dependencies")
            return False
            
        # Verify migration effort estimation
        if not re.search(r'\d+.*day', content.lower()):
            self.log_result("AC3: Migration Effort", False, "Missing migration effort estimation")
            self.test_results["criteria_failed"].append("Dependency compatibility matrix created for all current dependencies")
            return False
            
        # Count table entries to ensure comprehensiveness
        table_pattern = r'\|.*\|.*\|.*\|.*\|'
        table_rows = re.findall(table_pattern, content)
        
        if len(table_rows) < 15:  # Should have substantial number of dependencies
            self.log_result("AC3: Matrix Comprehensiveness", False,
                          f"Only {len(table_rows)} dependency entries found, expected 15+ for comprehensive coverage")
            self.test_results["criteria_failed"].append("Dependency compatibility matrix created for all current dependencies")
            return False
            
        self.log_result("AC3: Dependency Matrix Complete", True,
                       f"Comprehensive matrix with {len(table_rows)} dependencies, compatibility status, versions, and CVE information")
        self.test_results["criteria_met"].append("Dependency compatibility matrix created for all current dependencies")
        return True
        
    def test_overall_quality_and_integration(self):
        """Test overall quality and integration between documents"""
        
        # Verify all three documents exist
        required_docs = [
            "SPRING_BOOT_3_COMPATIBILITY_ASSESSMENT.md",
            "JAVAX_TO_JAKARTA_MIGRATION_CHECKLIST.md", 
            "DEPENDENCY_COMPATIBILITY_MATRIX.md"
        ]
        
        missing_docs = []
        total_size = 0
        
        for doc in required_docs:
            if os.path.exists(doc):
                total_size += os.path.getsize(doc)
            else:
                missing_docs.append(doc)
                
        if missing_docs:
            self.log_result("Document Completeness", False,
                          f"Missing documents: {', '.join(missing_docs)}")
            return False
            
        # Verify substantial content (comprehensive documentation should be sizeable)
        if total_size < 25000:  # ~25KB minimum for thorough documentation
            self.log_result("Content Depth", False,
                          f"Combined document size {total_size} bytes indicates insufficient detail")
            return False
            
        # Verify cross-document consistency for key recommendations
        all_content = ""
        for doc in required_docs:
            if os.path.exists(doc):
                with open(doc, 'r') as f:
                    all_content += f.read() + "\n"
                    
        # Check for consistent target versions
        if not re.search(r'java.*17', all_content.lower()):
            self.log_result("Java Version Consistency", False, "Missing consistent Java 17 recommendation")
            return False
            
        if not re.search(r'spring boot.*3', all_content.lower()):
            self.log_result("Spring Boot Version Consistency", False, "Missing consistent Spring Boot 3.x recommendation")
            return False
            
        self.log_result("Overall Quality", True,
                       f"All documents present with substantial content ({total_size} bytes) and consistent recommendations")
        return True
        
    def run_all_tests(self):
        """Run all tests and return results"""
        print("Running Final Spring Boot 3.x Compatibility Assessment Verification...")
        print("=" * 70)
        
        # Test each acceptance criteria
        ac1_result = self.test_acceptance_criteria_1_compatibility_assessment()
        ac2_result = self.test_acceptance_criteria_2_migration_checklist()
        ac3_result = self.test_acceptance_criteria_3_dependency_matrix()
        quality_result = self.test_overall_quality_and_integration()
        
        # Print detailed results
        print("\nDetailed Test Results:")
        print("-" * 50)
        for output in self.test_results["test_output"]:
            print(output)
            
        print(f"\nAcceptance Criteria Summary:")
        print(f"✅ Criteria Met: {len(self.test_results['criteria_met'])}")
        for criterion in self.test_results["criteria_met"]:
            print(f"   • {criterion}")
            
        if self.test_results["criteria_failed"]:
            print(f"❌ Criteria Failed: {len(self.test_results['criteria_failed'])}")
            for criterion in self.test_results["criteria_failed"]:
                print(f"   • {criterion}")
        
        overall_pass = ac1_result and ac2_result and ac3_result and quality_result
        print(f"\n🎯 Overall Assessment: {'PASS - All acceptance criteria met' if overall_pass else 'FAIL - Some criteria not met'}")
        
        # Update final result
        self.test_results["tests_passed"] = overall_pass
        
        return self.test_results

if __name__ == "__main__":
    tester = SpringBoot3CompatibilityFinalTest()
    results = tester.run_all_tests()
    
    # Output final JSON results
    print("\n" + "=" * 70)
    print("FINAL VERIFICATION RESULTS (JSON):")
    print(json.dumps(results, indent=2))