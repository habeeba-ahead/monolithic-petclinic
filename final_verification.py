#!/usr/bin/env python3
"""
Final verification script for Spring Boot upgrade to 2.7.18
Tests all acceptance criteria with actual build verification
"""

import subprocess
import re
import json
import sys
import os

def test_spring_boot_parent_version():
    """Test that Spring Boot parent version is updated to 2.7.18"""
    print("Testing Spring Boot parent version...")
    
    try:
        with open('pom.xml', 'r') as f:
            content = f.read()
        
        # Look for Spring Boot parent section and version
        parent_pattern = r'<parent>.*?<groupId>org\.springframework\.boot</groupId>.*?<artifactId>spring-boot-starter-parent</artifactId>.*?<version>(.*?)</version>.*?</parent>'
        match = re.search(parent_pattern, content, re.DOTALL)
        
        if not match:
            return False, "Spring Boot parent section not found in pom.xml"
        
        version = match.group(1).strip()
        expected_version = "2.7.18"
        
        if version == expected_version:
            return True, f"Spring Boot parent version correctly set to {version}"
        else:
            return False, f"Expected version {expected_version}, but found {version}"
            
    except Exception as e:
        return False, f"Error reading pom.xml: {str(e)}"

def test_spring_boot_dependencies_versions():
    """Test that all Spring Boot dependencies are using 2.7.x versions"""
    print("Testing Spring Boot dependencies versions...")
    
    try:
        # Run dependency tree and filter for Spring Boot dependencies
        result = subprocess.run(
            ['./mvnw', 'dependency:tree'],
            capture_output=True,
            text=True,
            timeout=120
        )
        
        if result.returncode != 0:
            return False, f"Failed to run dependency:tree: {result.stderr}"
        
        output = result.stdout
        
        # Extract Spring Boot dependency versions
        spring_boot_pattern = r'org\.springframework\.boot:[^:]+:jar:([^:]+):'
        versions = re.findall(spring_boot_pattern, output)
        
        if not versions:
            return False, "No Spring Boot dependencies found in dependency tree"
        
        # Check that all versions are 2.7.x
        non_27_versions = [v for v in versions if not v.startswith('2.7')]
        
        if non_27_versions:
            return False, f"Found Spring Boot dependencies not using 2.7.x versions: {set(non_27_versions)}"
        
        unique_versions = list(set(versions))
        return True, f"All {len(versions)} Spring Boot dependencies using 2.7.x versions: {unique_versions}"
        
    except subprocess.TimeoutExpired:
        return False, "Dependency tree command timed out"
    except Exception as e:
        return False, f"Error checking dependency versions: {str(e)}"

def test_project_builds_successfully():
    """Test that project builds successfully with mvn clean compile"""
    print("Testing project build...")
    
    try:
        # Run mvn clean compile
        result = subprocess.run(
            ['./mvnw', 'clean', 'compile'],
            capture_output=True,
            text=True,
            timeout=300  # 5 minutes timeout
        )
        
        if result.returncode == 0:
            # Check for BUILD SUCCESS in output
            if "BUILD SUCCESS" in result.stdout:
                return True, "Project builds successfully with mvn clean compile - BUILD SUCCESS confirmed"
            else:
                return True, "Project builds successfully with mvn clean compile"
        else:
            return False, f"Build failed with return code {result.returncode}. Error: {result.stderr[-500:]}"  # Last 500 chars of error
            
    except subprocess.TimeoutExpired:
        return False, "Build command timed out after 5 minutes"
    except Exception as e:
        return False, f"Error running build: {str(e)}"

def main():
    """Run all tests and report results"""
    print("=== Final Spring Boot 2.7.18 Upgrade Verification ===\n")
    
    tests = [
        ("Spring Boot parent version updated to 2.7.18 in root pom.xml", test_spring_boot_parent_version),
        ("All Spring Boot dependencies automatically updated to 2.7.x versions", test_spring_boot_dependencies_versions),
        ("Project builds successfully with mvn clean compile", test_project_builds_successfully)
    ]
    
    results = []
    criteria_met = []
    criteria_failed = []
    all_passed = True
    
    for test_name, test_func in tests:
        print(f"Running: {test_name}")
        try:
            passed, message = test_func()
            results.append(f"{test_name}: {'PASS' if passed else 'FAIL'} - {message}")
            
            if passed:
                criteria_met.append(test_name)
            else:
                criteria_failed.append(test_name)
                all_passed = False
                
        except Exception as e:
            error_msg = f"Test error: {str(e)}"
            results.append(f"{test_name}: ERROR - {error_msg}")
            criteria_failed.append(test_name)
            all_passed = False
        
        print(f"  Result: {'PASS' if test_name in criteria_met else 'FAIL'}")
        print()
    
    # Print summary
    print("=== Test Results Summary ===")
    for result in results:
        print(result)
    
    # Output JSON summary
    summary = {
        "tests_passed": all_passed,
        "test_output": "\n".join(results),
        "criteria_met": criteria_met,
        "criteria_failed": criteria_failed
    }
    
    print("\n=== JSON Summary ===")
    print(json.dumps(summary, indent=2))
    
    return 0 if all_passed else 1

if __name__ == "__main__":
    sys.exit(main())