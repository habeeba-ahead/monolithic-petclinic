#!/usr/bin/env python3
"""
Test script to verify Spring Boot upgrade to 2.7.18
"""

import xml.etree.ElementTree as ET
import subprocess
import sys
import re
import os

def test_spring_boot_parent_version():
    """Test that Spring Boot parent version is updated to 2.7.18 in root pom.xml"""
    print("Testing Spring Boot parent version...")
    
    try:
        tree = ET.parse('pom.xml')
        root = tree.getroot()
        
        # Find the parent section
        parent = root.find('.//{http://maven.apache.org/POM/4.0.0}parent')
        if parent is None:
            return False, "No parent section found in pom.xml"
        
        # Check groupId
        group_id = parent.find('.//{http://maven.apache.org/POM/4.0.0}groupId')
        if group_id is None or group_id.text != 'org.springframework.boot':
            return False, f"Parent groupId is not org.springframework.boot, found: {group_id.text if group_id is not None else 'None'}"
        
        # Check artifactId
        artifact_id = parent.find('.//{http://maven.apache.org/POM/4.0.0}artifactId')
        if artifact_id is None or artifact_id.text != 'spring-boot-starter-parent':
            return False, f"Parent artifactId is not spring-boot-starter-parent, found: {artifact_id.text if artifact_id is not None else 'None'}"
        
        # Check version
        version = parent.find('.//{http://maven.apache.org/POM/4.0.0}version')
        if version is None or version.text != '2.7.18':
            return False, f"Parent version is not 2.7.18, found: {version.text if version is not None else 'None'}"
        
        return True, "Spring Boot parent version is correctly set to 2.7.18"
        
    except Exception as e:
        return False, f"Error parsing pom.xml: {str(e)}"

def test_spring_boot_dependencies_version():
    """Test that Spring Boot dependencies are automatically updated to 2.7.x versions"""
    print("Testing Spring Boot dependencies versions...")
    
    try:
        # Use Maven wrapper to run dependency:tree
        result = subprocess.run(['./mvnw', 'dependency:tree', '-Dverbose', '-DoutputFile=dependency-tree.txt'], 
                              capture_output=True, text=True, timeout=300)
        
        if result.returncode != 0:
            return False, f"Failed to run ./mvnw dependency:tree: {result.stderr}"
        
        # Read the dependency tree
        with open('dependency-tree.txt', 'r') as f:
            dependency_tree = f.read()
        
        # Look for Spring Boot dependencies and check their versions
        spring_boot_deps = []
        for line in dependency_tree.split('\n'):
            if 'org.springframework.boot:' in line and '2.7.' in line:
                spring_boot_deps.append(line.strip())
        
        if not spring_boot_deps:
            return False, "No Spring Boot 2.7.x dependencies found in dependency tree"
        
        # Check that all found Spring Boot dependencies are 2.7.x
        non_27_deps = []
        for dep in spring_boot_deps:
            if not re.search(r'2\.7\.\d+', dep):
                non_27_deps.append(dep)
        
        if non_27_deps:
            return False, f"Found Spring Boot dependencies not on 2.7.x: {non_27_deps}"
        
        return True, f"Found {len(spring_boot_deps)} Spring Boot dependencies on 2.7.x versions"
        
    except subprocess.TimeoutExpired:
        return False, "Maven dependency:tree command timed out"
    except Exception as e:
        return False, f"Error checking Spring Boot dependencies: {str(e)}"

def test_project_builds_successfully():
    """Test that project builds successfully with mvn clean compile"""
    print("Testing project build...")
    
    try:
        # Use Maven wrapper to run clean compile
        result = subprocess.run(['./mvnw', 'clean', 'compile'], 
                              capture_output=True, text=True, timeout=600)
        
        if result.returncode != 0:
            return False, f"Build failed with exit code {result.returncode}. Error: {result.stderr}"
        
        # Check for BUILD SUCCESS in output
        if 'BUILD SUCCESS' in result.stdout:
            return True, "Project builds successfully with mvn clean compile"
        else:
            return False, f"Build did not show SUCCESS. Output: {result.stdout[-500:]}"
        
    except subprocess.TimeoutExpired:
        return False, "Maven build command timed out"
    except Exception as e:
        return False, f"Error running build: {str(e)}"

def verify_pom_structure():
    """Additional verification of pom.xml structure and Spring Boot dependencies"""
    print("Verifying pom.xml structure and Spring Boot dependencies...")
    
    try:
        # Check root pom.xml
        tree = ET.parse('pom.xml')
        root = tree.getroot()
        
        # Verify it's a multi-module project
        modules = root.findall('.//{http://maven.apache.org/POM/4.0.0}module')
        module_names = [m.text for m in modules]
        
        if 'activemq' not in module_names or 'monolith' not in module_names:
            return False, f"Expected modules 'activemq' and 'monolith', found: {module_names}"
        
        # Check module pom.xml files for Spring Boot dependencies
        spring_boot_deps_found = []
        
        for module in ['activemq', 'monolith']:
            module_pom = f"{module}/pom.xml"
            if os.path.exists(module_pom):
                module_tree = ET.parse(module_pom)
                module_root = module_tree.getroot()
                
                # Find Spring Boot dependencies
                deps = module_root.findall('.//{http://maven.apache.org/POM/4.0.0}dependency')
                for dep in deps:
                    group_id = dep.find('.//{http://maven.apache.org/POM/4.0.0}groupId')
                    artifact_id = dep.find('.//{http://maven.apache.org/POM/4.0.0}artifactId')
                    
                    if (group_id is not None and group_id.text == 'org.springframework.boot' and
                        artifact_id is not None):
                        spring_boot_deps_found.append(f"{module}:{artifact_id.text}")
        
        if not spring_boot_deps_found:
            return False, "No Spring Boot dependencies found in module pom.xml files"
        
        return True, f"Found Spring Boot dependencies in modules: {spring_boot_deps_found}"
        
    except Exception as e:
        return False, f"Error verifying pom structure: {str(e)}"

def main():
    """Run all tests and report results"""
    tests = [
        ("Spring Boot parent version updated to 2.7.18 in root pom.xml", test_spring_boot_parent_version),
        ("All Spring Boot dependencies automatically updated to 2.7.x versions", test_spring_boot_dependencies_version),
        ("Project builds successfully with mvn clean compile", test_project_builds_successfully),
        ("POM structure verification", verify_pom_structure)
    ]
    
    results = []
    criteria_met = []
    criteria_failed = []
    all_passed = True
    
    for test_name, test_func in tests:
        print(f"\n--- {test_name} ---")
        try:
            passed, message = test_func()
            results.append(f"{test_name}: {'PASS' if passed else 'FAIL'} - {message}")
            
            if passed:
                criteria_met.append(test_name)
            else:
                criteria_failed.append(test_name)
                all_passed = False
                
            print(f"Result: {'PASS' if passed else 'FAIL'}")
            print(f"Message: {message}")
            
        except Exception as e:
            error_msg = f"Test error: {str(e)}"
            results.append(f"{test_name}: ERROR - {error_msg}")
            criteria_failed.append(test_name)
            all_passed = False
            print(f"Result: ERROR")
            print(f"Message: {error_msg}")
    
    # Print summary
    print(f"\n{'='*60}")
    print("TEST SUMMARY")
    print(f"{'='*60}")
    for result in results:
        print(result)
    
    print(f"\nOverall result: {'ALL TESTS PASSED' if all_passed else 'SOME TESTS FAILED'}")
    
    # Return results for JSON output
    return {
        "tests_passed": all_passed,
        "test_output": "\n".join(results),
        "criteria_met": criteria_met,
        "criteria_failed": criteria_failed
    }

if __name__ == "__main__":
    result = main()
    
    # Output JSON result
    import json
    print(f"\n{'='*60}")
    print("JSON RESULT")
    print(f"{'='*60}")
    print(json.dumps(result, indent=2))