#!/usr/bin/env python3
"""
Verification script for Spring Boot upgrade to 2.7.18
Tests all acceptance criteria:
1. Spring Boot parent version updated to 2.7.18 in root pom.xml
2. All Spring Boot dependencies automatically updated to 2.7.x versions
3. Project builds successfully with mvn clean compile
"""

import subprocess
import xml.etree.ElementTree as ET
import re
import json
import sys

def test_spring_boot_parent_version():
    """Test that Spring Boot parent version is updated to 2.7.18"""
    print("Testing Spring Boot parent version...")
    
    try:
        tree = ET.parse('pom.xml')
        root = tree.getroot()
        
        # Find parent version
        ns = {'maven': 'http://maven.apache.org/POM/4.0.0'}
        parent = root.find('maven:parent', ns) or root.find('parent')
        
        if parent is None:
            return False, "No parent section found in pom.xml"
            
        version_elem = parent.find('maven:version', ns) or parent.find('version')
        if version_elem is None:
            return False, "No version found in parent section"
            
        version = version_elem.text.strip()
        expected_version = "2.7.18"
        
        if version == expected_version:
            return True, f"Spring Boot parent version correctly set to {version}"
        else:
            return False, f"Expected version {expected_version}, but found {version}"
            
    except Exception as e:
        return False, f"Error parsing pom.xml: {str(e)}"

def test_spring_boot_dependencies_versions():
    """Test that Spring Boot dependencies are using 2.7.x versions"""
    print("Testing Spring Boot dependencies versions...")
    
    try:
        # Run mvn dependency:tree to get actual resolved versions
        result = subprocess.run(
            ['mvn', 'dependency:tree', '-Dverbose', '-DoutputType=text'],
            capture_output=True,
            text=True,
            timeout=300
        )
        
        if result.returncode != 0:
            return False, f"Failed to run dependency:tree: {result.stderr}"
        
        output = result.stdout
        
        # Look for Spring Boot dependencies and their versions
        spring_boot_deps = []
        lines = output.split('\n')
        
        for line in lines:
            if 'org.springframework.boot:' in line and ':jar:' in line:
                # Extract version from dependency tree output
                match = re.search(r'org\.springframework\.boot:[^:]+:jar:([^:]+)', line)
                if match:
                    version = match.group(1)
                    spring_boot_deps.append(version)
        
        if not spring_boot_deps:
            return False, "No Spring Boot dependencies found in dependency tree"
        
        # Check that all versions start with 2.7
        non_27_versions = [v for v in spring_boot_deps if not v.startswith('2.7')]
        
        if non_27_versions:
            return False, f"Found Spring Boot dependencies not using 2.7.x versions: {non_27_versions}"
        
        unique_versions = list(set(spring_boot_deps))
        return True, f"All Spring Boot dependencies using 2.7.x versions: {unique_versions}"
        
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
            ['mvn', 'clean', 'compile'],
            capture_output=True,
            text=True,
            timeout=600  # 10 minutes timeout
        )
        
        if result.returncode == 0:
            return True, "Project builds successfully with mvn clean compile"
        else:
            return False, f"Build failed with return code {result.returncode}. Error: {result.stderr[-1000:]}"  # Last 1000 chars of error
            
    except subprocess.TimeoutExpired:
        return False, "Build command timed out after 10 minutes"
    except Exception as e:
        return False, f"Error running build: {str(e)}"

def main():
    """Run all tests and report results"""
    print("=== Spring Boot 2.7.18 Upgrade Verification ===\n")
    
    tests = [
        ("Spring Boot parent version updated to 2.7.18", test_spring_boot_parent_version),
        ("Spring Boot dependencies using 2.7.x versions", test_spring_boot_dependencies_versions),
        ("Project builds successfully", test_project_builds_successfully)
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