#!/usr/bin/env python3
"""
Test suite to verify Spring Boot 3.x compatibility assessment acceptance criteria.
"""

import os
import re
import json
import subprocess
from pathlib import Path

class SpringBoot3CompatibilityTest:
    def __init__(self):
        self.repo_root = Path(".")
        self.results = {
            "tests_passed": True,
            "test_output": [],
            "criteria_met": [],
            "criteria_failed": []
        }
    
    def log(self, message):
        """Log test output"""
        print(message)
        self.results["test_output"].append(message)
    
    def fail_criterion(self, criterion, reason):
        """Mark a criterion as failed"""
        self.results["tests_passed"] = False
        self.results["criteria_failed"].append(f"{criterion}: {reason}")
        self.log(f"❌ FAILED: {criterion} - {reason}")
    
    def pass_criterion(self, criterion):
        """Mark a criterion as passed"""
        self.results["criteria_met"].append(criterion)
        self.log(f"✅ PASSED: {criterion}")
    
    def test_javax_inventory_documented(self):
        """Test: Complete inventory of javax.* package usage documented"""
        self.log("\n=== Testing javax.* Inventory Documentation ===")
        
        # Check if inventory file exists
        inventory_file = self.repo_root / "JAVAX_IMPORTS_INVENTORY.md"
        if not inventory_file.exists():
            self.fail_criterion("javax.* inventory documented", "JAVAX_IMPORTS_INVENTORY.md file not found")
            return
        
        # Read inventory content
        content = inventory_file.read_text()
        
        # Verify required sections exist
        required_sections = [
            "## Summary",
            "## Detailed Inventory", 
            "### javax.persistence Package",
            "### javax.validation Package",
            "## Migration Mapping"
        ]
        
        missing_sections = []
        for section in required_sections:
            if section not in content:
                missing_sections.append(section)
        
        if missing_sections:
            self.fail_criterion("javax.* inventory documented", f"Missing sections: {missing_sections}")
            return
        
        # Verify specific javax packages are documented
        if "javax.persistence" not in content or "javax.validation" not in content:
            self.fail_criterion("javax.* inventory documented", "Missing javax.persistence or javax.validation documentation")
            return
        
        # Verify file count and import count are documented
        if "Total Files" not in content or "Total Imports" not in content:
            self.fail_criterion("javax.* inventory documented", "Missing total files or imports count")
            return
        
        # Verify migration mapping exists
        if "jakarta.persistence" not in content or "jakarta.validation" not in content:
            self.fail_criterion("javax.* inventory documented", "Missing jakarta migration mapping")
            return
        
        self.pass_criterion("Complete inventory of javax.* package usage documented")
    
    def test_incompatible_dependencies_identified(self):
        """Test: List of incompatible dependencies identified"""
        self.log("\n=== Testing Incompatible Dependencies Identification ===")
        
        # Check main assessment file
        assessment_file = self.repo_root / "SPRING_BOOT_3_COMPATIBILITY_ASSESSMENT.md"
        if not assessment_file.exists():
            self.fail_criterion("incompatible dependencies identified", "Assessment file not found")
            return
        
        content = assessment_file.read_text()
        
        # Check for incompatible dependencies section
        if "## 2. Incompatible Dependencies Identified" not in content:
            self.fail_criterion("incompatible dependencies identified", "Incompatible dependencies section missing")
            return
        
        # Verify key incompatible dependencies are identified
        required_dependencies = [
            "Java Version Compatibility",
            "Spring Boot Version", 
            "WireMock",
            "WebJars"
        ]
        
        missing_deps = []
        for dep in required_dependencies:
            if dep not in content:
                missing_deps.append(dep)
        
        if missing_deps:
            self.fail_criterion("incompatible dependencies identified", f"Missing dependencies: {missing_deps}")
            return
        
        # Verify Java 17 requirement is mentioned
        if "Java 17" not in content:
            self.fail_criterion("incompatible dependencies identified", "Java 17 requirement not documented")
            return
        
        # Verify Spring Boot 3.x requirement is mentioned
        if "Spring Boot 3" not in content:
            self.fail_criterion("incompatible dependencies identified", "Spring Boot 3.x requirement not documented")
            return
        
        self.pass_criterion("List of incompatible dependencies identified")
    
    def test_breaking_changes_assessment(self):
        """Test: Breaking changes assessment completed"""
        self.log("\n=== Testing Breaking Changes Assessment ===")
        
        assessment_file = self.repo_root / "SPRING_BOOT_3_COMPATIBILITY_ASSESSMENT.md"
        if not assessment_file.exists():
            self.fail_criterion("breaking changes assessment", "Assessment file not found")
            return
        
        content = assessment_file.read_text()
        
        # Check for breaking changes section
        if "## 3. Breaking Changes Assessment" not in content:
            self.fail_criterion("breaking changes assessment", "Breaking changes section missing")
            return
        
        # Verify key breaking changes are assessed
        required_changes = [
            "Package Migration (javax → jakarta)",
            "Configuration Changes",
            "Repository Layer",
            "Web Layer",
            "Build Configuration"
        ]
        
        missing_changes = []
        for change in required_changes:
            if change not in content:
                missing_changes.append(change)
        
        if missing_changes:
            self.fail_criterion("breaking changes assessment", f"Missing breaking changes: {missing_changes}")
            return
        
        # Verify effort and risk assessments exist
        if "Effort" not in content or "Risk" not in content:
            self.fail_criterion("breaking changes assessment", "Missing effort or risk assessments")
            return
        
        # Verify javax to jakarta migration is specifically mentioned
        if "javax → jakarta" not in content:
            self.fail_criterion("breaking changes assessment", "javax to jakarta migration not documented")
            return
        
        self.pass_criterion("Breaking changes assessment completed")
    
    def test_migration_strategy_documented(self):
        """Test: Migration strategy documented"""
        self.log("\n=== Testing Migration Strategy Documentation ===")
        
        assessment_file = self.repo_root / "SPRING_BOOT_3_COMPATIBILITY_ASSESSMENT.md"
        if not assessment_file.exists():
            self.fail_criterion("migration strategy documented", "Assessment file not found")
            return
        
        content = assessment_file.read_text()
        
        # Check for migration strategy section
        if "## 4. Migration Strategy" not in content:
            self.fail_criterion("migration strategy documented", "Migration strategy section missing")
            return
        
        # Verify migration phases are documented
        required_phases = [
            "Phase 1",
            "Phase 2", 
            "Phase 3",
            "Phase 4"
        ]
        
        missing_phases = []
        for phase in required_phases:
            if phase not in content:
                missing_phases.append(phase)
        
        if missing_phases:
            self.fail_criterion("migration strategy documented", f"Missing migration phases: {missing_phases}")
            return
        
        # Verify key migration activities are documented
        required_activities = [
            "Java Runtime Upgrade",
            "Spring Boot Upgrade",
            "Package Migration",
            "Testing and Validation"
        ]
        
        missing_activities = []
        for activity in required_activities:
            if activity not in content:
                missing_activities.append(activity)
        
        if missing_activities:
            self.fail_criterion("migration strategy documented", f"Missing activities: {missing_activities}")
            return
        
        # Verify timeline is provided
        if "Timeline Estimate" not in content:
            self.fail_criterion("migration strategy documented", "Timeline estimate missing")
            return
        
        # Verify risk assessment is included
        if "Risk Assessment" not in content:
            self.fail_criterion("migration strategy documented", "Risk assessment missing")
            return
        
        # Verify success criteria are defined
        if "Success Criteria" not in content:
            self.fail_criterion("migration strategy documented", "Success criteria missing")
            return
        
        self.pass_criterion("Migration strategy documented")
    
    def test_actual_javax_usage_verification(self):
        """Test: Verify actual javax.* usage in codebase matches inventory"""
        self.log("\n=== Testing Actual javax.* Usage Verification ===")
        
        # Find all Java files
        java_files = list(self.repo_root.glob("**/*.java"))
        
        # Count javax imports in actual files
        javax_files = {}
        total_javax_imports = 0
        
        for java_file in java_files:
            try:
                content = java_file.read_text()
                javax_imports = re.findall(r'import\s+javax\.(\w+(?:\.\w+)*)', content)
                if javax_imports:
                    javax_files[str(java_file)] = javax_imports
                    total_javax_imports += len(javax_imports)
            except Exception as e:
                self.log(f"Warning: Could not read {java_file}: {e}")
        
        self.log(f"Found {total_javax_imports} javax.* imports in {len(javax_files)} files")
        
        # Verify inventory matches reality
        inventory_file = self.repo_root / "JAVAX_IMPORTS_INVENTORY.md"
        if inventory_file.exists():
            inventory_content = inventory_file.read_text()
            
            # Check if documented count matches actual count
            if "41 javax.* import statements" in inventory_content and total_javax_imports != 41:
                self.fail_criterion("javax.* inventory accuracy", 
                                  f"Documented 41 imports but found {total_javax_imports}")
                return
            
            # Check if documented file count matches
            if "13 files contain javax.* imports" in inventory_content and len(javax_files) != 13:
                self.fail_criterion("javax.* inventory accuracy",
                                  f"Documented 13 files but found {len(javax_files)}")
                return
        
        # Verify expected javax packages are found
        expected_packages = ["persistence", "validation"]
        found_packages = set()
        
        for imports in javax_files.values():
            for imp in imports:
                package = imp.split('.')[0]
                found_packages.add(package)
        
        missing_packages = set(expected_packages) - found_packages
        if missing_packages:
            self.fail_criterion("javax.* inventory accuracy", 
                              f"Expected packages not found: {missing_packages}")
            return
        
        self.pass_criterion("javax.* inventory matches actual codebase usage")
    
    def test_pom_analysis(self):
        """Test: Verify POM files are analyzed for Spring Boot version"""
        self.log("\n=== Testing POM Analysis ===")
        
        # Check main pom.xml
        main_pom = self.repo_root / "pom.xml"
        if not main_pom.exists():
            self.fail_criterion("POM analysis", "Main pom.xml not found")
            return
        
        # Check monolith pom.xml
        monolith_pom = self.repo_root / "monolith" / "pom.xml"
        if not monolith_pom.exists():
            self.fail_criterion("POM analysis", "Monolith pom.xml not found")
            return
        
        # Read and check Spring Boot version in monolith POM
        monolith_content = monolith_pom.read_text()
        
        # Look for Spring Boot version
        spring_boot_version_match = re.search(r'<version>([^<]+)</version>', monolith_content)
        if not spring_boot_version_match:
            self.fail_criterion("POM analysis", "Could not find Spring Boot version in monolith POM")
            return
        
        # Verify assessment mentions the current version
        assessment_file = self.repo_root / "SPRING_BOOT_3_COMPATIBILITY_ASSESSMENT.md"
        if assessment_file.exists():
            assessment_content = assessment_file.read_text()
            if "2.2.1.RELEASE" not in assessment_content:
                self.fail_criterion("POM analysis", "Current Spring Boot version not documented in assessment")
                return
        
        self.pass_criterion("POM files analyzed for Spring Boot compatibility")
    
    def test_comprehensive_assessment_quality(self):
        """Test: Verify the assessment is comprehensive and actionable"""
        self.log("\n=== Testing Assessment Comprehensiveness ===")
        
        assessment_file = self.repo_root / "SPRING_BOOT_3_COMPATIBILITY_ASSESSMENT.md"
        if not assessment_file.exists():
            self.fail_criterion("comprehensive assessment", "Assessment file not found")
            return
        
        content = assessment_file.read_text()
        
        # Check for all required major sections
        required_major_sections = [
            "## Executive Summary",
            "## 1. Complete Inventory of javax.* Package Usage",
            "## 2. Incompatible Dependencies Identified", 
            "## 3. Breaking Changes Assessment",
            "## 4. Migration Strategy",
            "## 5. Risk Assessment",
            "## 6. Recommended Tools",
            "## 7. Success Criteria",
            "## 8. Timeline Estimate"
        ]
        
        missing_major_sections = []
        for section in required_major_sections:
            if section not in content:
                missing_major_sections.append(section)
        
        if missing_major_sections:
            self.fail_criterion("comprehensive assessment", f"Missing major sections: {missing_major_sections}")
            return
        
        # Verify actionable content exists
        actionable_elements = [
            "OpenRewrite",  # Migration tool
            "Java 17",      # Target version
            "jakarta",      # Target package
            "Phase 1",      # Migration phases
            "days"          # Timeline estimates
        ]
        
        missing_actionable = []
        for element in actionable_elements:
            if element not in content:
                missing_actionable.append(element)
        
        if missing_actionable:
            self.fail_criterion("comprehensive assessment", f"Missing actionable elements: {missing_actionable}")
            return
        
        self.pass_criterion("Assessment is comprehensive and actionable")
    
    def run_all_tests(self):
        """Run all compatibility assessment tests"""
        self.log("Starting Spring Boot 3.x Compatibility Assessment Verification")
        self.log("=" * 70)
        
        # Run all test methods
        test_methods = [
            self.test_javax_inventory_documented,
            self.test_incompatible_dependencies_identified, 
            self.test_breaking_changes_assessment,
            self.test_migration_strategy_documented,
            self.test_actual_javax_usage_verification,
            self.test_pom_analysis,
            self.test_comprehensive_assessment_quality
        ]
        
        for test_method in test_methods:
            try:
                test_method()
            except Exception as e:
                self.fail_criterion(test_method.__name__, f"Test execution failed: {str(e)}")
        
        # Summary
        self.log("\n" + "=" * 70)
        self.log("VERIFICATION SUMMARY")
        self.log("=" * 70)
        
        if self.results["tests_passed"]:
            self.log("🎉 ALL ACCEPTANCE CRITERIA MET!")
        else:
            self.log("❌ SOME ACCEPTANCE CRITERIA FAILED")
        
        self.log(f"\nCriteria Met: {len(self.results['criteria_met'])}")
        for criterion in self.results["criteria_met"]:
            self.log(f"  ✅ {criterion}")
        
        if self.results["criteria_failed"]:
            self.log(f"\nCriteria Failed: {len(self.results['criteria_failed'])}")
            for criterion in self.results["criteria_failed"]:
                self.log(f"  ❌ {criterion}")
        
        return self.results

if __name__ == "__main__":
    tester = SpringBoot3CompatibilityTest()
    results = tester.run_all_tests()
    
    # Output JSON results
    print("\n" + "=" * 70)
    print("JSON RESULTS")
    print("=" * 70)
    print(json.dumps(results, indent=2))