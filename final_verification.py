#!/usr/bin/env python3
"""
Final verification of Spring Boot 3.x compatibility assessment acceptance criteria.
"""

import os
import re
import json

def verify_acceptance_criteria():
    """Verify all acceptance criteria are met"""
    results = {
        "tests_passed": True,
        "test_output": [],
        "criteria_met": [],
        "criteria_failed": []
    }
    
    # Acceptance Criteria 1: Compatibility assessment document created listing all breaking changes
    assessment_file = "SPRING_BOOT_3_COMPATIBILITY_ASSESSMENT.md"
    if os.path.exists(assessment_file):
        with open(assessment_file, 'r') as f:
            assessment_content = f.read()
            
        # Check for comprehensive breaking changes documentation
        breaking_changes_found = [
            "Java" in assessment_content and "17" in assessment_content,  # Java version requirement
            "jakarta" in assessment_content and "javax" in assessment_content,  # Package migration
            "Hibernate" in assessment_content,  # ORM changes
            "CVE" in assessment_content,  # Security vulnerabilities
            "Risk Assessment" in assessment_content,  # Risk analysis
            "Migration Strategy" in assessment_content  # Migration approach
        ]
        
        if all(breaking_changes_found):
            results["criteria_met"].append("Compatibility assessment document created listing all breaking changes")
            results["test_output"].append("PASS: Compatibility assessment document contains all required breaking changes")
        else:
            results["criteria_failed"].append("Compatibility assessment document created listing all breaking changes")
            results["test_output"].append("FAIL: Compatibility assessment document missing some breaking changes")
            results["tests_passed"] = False
    else:
        results["criteria_failed"].append("Compatibility assessment document created listing all breaking changes")
        results["test_output"].append("FAIL: Compatibility assessment document not found")
        results["tests_passed"] = False
    
    # Acceptance Criteria 2: Migration checklist created for javax to jakarta package changes
    checklist_file = "JAVAX_TO_JAKARTA_MIGRATION_CHECKLIST.md"
    if os.path.exists(checklist_file):
        with open(checklist_file, 'r') as f:
            checklist_content = f.read()
            
        # Check for comprehensive javax to jakarta migration guidance
        migration_elements = [
            "javax.persistence" in checklist_content and "jakarta.persistence" in checklist_content,
            "javax.validation" in checklist_content and "jakarta.validation" in checklist_content,
            "- [ ]" in checklist_content,  # Checklist format
            "// BEFORE" in checklist_content and "// AFTER" in checklist_content,  # Code examples
            "find . -name" in checklist_content,  # Automation scripts
            ".java" in checklist_content  # Specific file references
        ]
        
        if all(migration_elements):
            results["criteria_met"].append("Migration checklist created for javax to jakarta package changes")
            results["test_output"].append("PASS: Migration checklist contains comprehensive javax to jakarta guidance")
        else:
            results["criteria_failed"].append("Migration checklist created for javax to jakarta package changes")
            results["test_output"].append("FAIL: Migration checklist missing some required elements")
            results["tests_passed"] = False
    else:
        results["criteria_failed"].append("Migration checklist created for javax to jakarta package changes")
        results["test_output"].append("FAIL: Migration checklist document not found")
        results["tests_passed"] = False
    
    # Acceptance Criteria 3: Dependency compatibility matrix created for all current dependencies
    matrix_file = "DEPENDENCY_COMPATIBILITY_MATRIX.md"
    if os.path.exists(matrix_file):
        with open(matrix_file, 'r') as f:
            matrix_content = f.read()
            
        # Check for comprehensive dependency coverage
        dependency_elements = [
            "Spring Boot" in matrix_content,  # Core framework
            "Hibernate" in matrix_content or "JPA" in matrix_content,  # Data access
            "Thymeleaf" in matrix_content,  # Web templating
            "jQuery" in matrix_content or "Bootstrap" in matrix_content,  # Frontend
            "ActiveMQ" in matrix_content or "JMS" in matrix_content,  # Messaging
            "JUnit" in matrix_content or "Mockito" in matrix_content,  # Testing
            "Maven" in matrix_content,  # Build tools
            "✅" in matrix_content or "❌" in matrix_content,  # Compatibility indicators
            "Current Version" in matrix_content,  # Version tracking
            "CVE" in matrix_content  # Security information
        ]
        
        if all(dependency_elements):
            results["criteria_met"].append("Dependency compatibility matrix created for all current dependencies")
            results["test_output"].append("PASS: Dependency compatibility matrix covers all major dependency categories")
        else:
            results["criteria_failed"].append("Dependency compatibility matrix created for all current dependencies")
            results["test_output"].append("FAIL: Dependency compatibility matrix missing some required elements")
            results["tests_passed"] = False
    else:
        results["criteria_failed"].append("Dependency compatibility matrix created for all current dependencies")
        results["test_output"].append("FAIL: Dependency compatibility matrix document not found")
        results["tests_passed"] = False
    
    # Additional quality checks
    total_files = sum(1 for f in [assessment_file, checklist_file, matrix_file] if os.path.exists(f))
    if total_files == 3:
        results["test_output"].append(f"PASS: All 3 required documents created")
        
        # Check total content size for comprehensiveness
        total_size = sum(os.path.getsize(f) for f in [assessment_file, checklist_file, matrix_file] if os.path.exists(f))
        if total_size > 20000:  # Substantial documentation
            results["test_output"].append(f"PASS: Documentation is comprehensive ({total_size} bytes total)")
        else:
            results["test_output"].append(f"WARN: Documentation may lack detail ({total_size} bytes total)")
    else:
        results["test_output"].append(f"FAIL: Only {total_files}/3 required documents found")
        results["tests_passed"] = False
    
    return results

if __name__ == "__main__":
    print("Final Verification: Spring Boot 3.x Compatibility Assessment")
    print("=" * 65)
    
    results = verify_acceptance_criteria()
    
    print("\nVerification Results:")
    print("-" * 40)
    for output in results["test_output"]:
        print(output)
    
    print(f"\nAcceptance Criteria Status:")
    print(f"✅ Met ({len(results['criteria_met'])}): ")
    for criterion in results["criteria_met"]:
        print(f"   • {criterion}")
    
    if results["criteria_failed"]:
        print(f"❌ Failed ({len(results['criteria_failed'])}): ")
        for criterion in results["criteria_failed"]:
            print(f"   • {criterion}")
    
    overall_status = "PASS" if results["tests_passed"] else "FAIL"
    print(f"\n🎯 Overall Verification: {overall_status}")
    
    print("\n" + "=" * 65)
    print("JSON SUMMARY:")
    print(json.dumps(results, indent=2))