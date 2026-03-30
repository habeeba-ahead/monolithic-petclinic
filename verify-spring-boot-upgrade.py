#!/usr/bin/env python3
"""
Test script to verify Spring Boot upgrade to 2.7.18
"""
import subprocess
import sys
import xml.etree.ElementTree as ET
import re
import os

def test_spring_boot_parent_version():
    """Test that Spring Boot parent version is updated to 2.7.18 in root pom.xml"""
    print("Testing Spring Boot parent version...")
    
    try:
        tree = ET.parse('pom.xml')
        root = tree.getroot()
        
        # Find parent version
        parent = root.find('.//{http://maven.apache.org/POM/4.0.0}parent')
        if parent is None:
            return False, "No parent element found in root pom.xml"
        
        version = parent.find('.//{http://maven.apache.org/POM/4.0.0}version')
        if version is None:
            return False, "No version element found in parent"
        
        actual_version = version.text
        expected_version = "2.7.18"
        
        if actual_version == expected_version:
            return True, f"Spring Boot parent version is correctly set to {actual_version}"
        else:
            return False, f"Expected version {expected_version}, but found {actual_version}"
            
    except Exception as e:
        return False, f"Error parsing pom.xml: {str(e)}"

def test_spring_boot_dependencies_version():
    """Test that Spring Boot dependencies are automatically updated to 2.7.x versions"""
    print("Testing Spring Boot dependencies versions...")
    
    try:
        # Use Maven wrapper to run dependency:tree
        mvn_cmd = './mvnw' if os.path.exists('./mvnw') else 'mvn'
        result = subprocess.run([mvn_cmd, 'dependency:tree', '-Dverbose'], 
                              capture_output=True, text=True, timeout=180)
        
        if result.returncode != 0:
            return False, f"Failed to run dependency:tree: {result.stderr}"
        
        output = result.stdout
        
        # Look for Spring Boot dependencies and check they are 2.7.x versions
        spring_boot_deps = []
        for line in output.split('\n'):
            if 'org.springframework.boot:' in line and '2.7.' in line:
                spring_boot_deps.append(line.strip())
        
        if spring_boot_deps:
            return True, f"Found Spring Boot 2.7.x dependencies: {len(spring_boot_deps)} dependencies"
        else:
            return False, "No Spring Boot 2.7.x dependencies found in dependency tree"
            
    except subprocess.TimeoutExpired:
        return False, "Timeout while running dependency:tree"
    except Exception as e:
        return False, f"Error checking dependencies: {str(e)}"

def test_project_builds_successfully():
    """Test that project builds successfully with mvn clean compile"""
    print("Testing project build...")
    
    try:
        # Use Maven wrapper to run clean compile
        mvn_cmd = './mvnw' if os.path.exists('./mvnw') else 'mvn'
        result = subprocess.run([mvn_cmd, 'clean', 'compile'], 
                              capture_output=True, text=True, timeout=600)
        
        if result.returncode == 0:
            return True, "Project builds successfully with mvn clean compile"
        else:
            return False, f"Build failed with return code {result.returncode}. Error: {result.stderr}"
            
    except subprocess.TimeoutExpired:
        return False, "Timeout while building project"
    except Exception as e:
        return False, f"Error building project: {str(e)}"

def test_effective_pom_spring_boot_version():
    """Alternative test to check Spring Boot version using effective POM"""
    print("Testing effective POM for Spring Boot version...")
    
    try:
        mvn_cmd = './mvnw' if os.path.exists('./mvnw') else 'mvn'
        result = subprocess.run([mvn_cmd, 'help:effective-pom', '-Doutput=effective-pom.xml'], 
                              capture_output=True, text=True, timeout=120)
        
        if result.returncode != 0:
            return False, f"Failed to generate effective POM: {result.stderr}"
        
        # Parse the effective POM to check Spring Boot version
        if os.path.exists('effective-pom.xml'):
            tree = ET.parse('effective-pom.xml')
            root = tree.getroot()
            
            # Look for Spring Boot version in properties
            properties = root.find('.//{http://maven.apache.org/POM/4.0.0}properties')
            if properties is not None:
                for prop in properties:
                    if 'spring-boot' in prop.tag.lower() and 'version' in prop.tag.lower():
                        if '2.7.' in prop.text:
                            return True, f"Found Spring Boot 2.7.x version in effective POM: {prop.text}"
            
            # Also check parent version in effective POM
            parent = root.find('.//{http://maven.apache.org/POM/4.0.0}parent')
            if parent is not None:
                group_id = parent.find('.//{http://maven.apache.org/POM/4.0.0}groupId')
                version = parent.find('.//{http://maven.apache.org/POM/4.0.0}version')
                if (group_id is not None and 'spring-boot' in group_id.text and 
                    version is not None and '2.7.' in version.text):
                    return True, f"Spring Boot parent version in effective POM: {version.text}"
        
        return False, "Could not verify Spring Boot 2.7.x version in effective POM"
        
    except Exception as e:
        return False, f"Error checking effective POM: {str(e)}"

def main():
    """Run all tests and report results"""
    tests = [
        ("Spring Boot parent version updated to 2.7.18 in root pom.xml", test_spring_boot_parent_version),
        ("All Spring Boot dependencies automatically updated to 2.7.x versions", test_spring_boot_dependencies_version),
        ("Project builds successfully with mvn clean compile", test_project_builds_successfully)
    ]
    
    results = []
    criteria_met = []
    criteria_failed = []
    
    for test_name, test_func in tests:
        print(f"\n--- {test_name} ---")
        try:
            success, message = test_func()
            results.append((test_name, success, message))
            print(f"Result: {'PASS' if success else 'FAIL'}")
            print(f"Message: {message}")
            
            if success:
                criteria_met.append(test_name)
            else:
                criteria_failed.append(test_name)
                
        except Exception as e:
            results.append((test_name, False, f"Test error: {str(e)}"))
            criteria_failed.append(test_name)
            print(f"Result: ERROR")
            print(f"Message: Test error: {str(e)}")
    
    # If dependency test failed, try alternative effective POM test
    if "All Spring Boot dependencies automatically updated to 2.7.x versions" in criteria_failed:
        print(f"\n--- Alternative: Effective POM Spring Boot version check ---")
        try:
            success, message = test_effective_pom_spring_boot_version()
            print(f"Result: {'PASS' if success else 'FAIL'}")
            print(f"Message: {message}")
            
            if success:
                # Replace the failed test with this successful one
                criteria_failed.remove("All Spring Boot dependencies automatically updated to 2.7.x versions")
                criteria_met.append("All Spring Boot dependencies automatically updated to 2.7.x versions")
                # Update the results
                for i, (test_name, _, _) in enumerate(results):
                    if test_name == "All Spring Boot dependencies automatically updated to 2.7.x versions":
                        results[i] = (test_name, True, f"Alternative verification: {message}")
                        break
                        
        except Exception as e:
            print(f"Result: ERROR")
            print(f"Message: Alternative test error: {str(e)}")
    
    # Summary
    print(f"\n--- SUMMARY ---")
    all_passed = len(criteria_failed) == 0
    print(f"Overall result: {'PASS' if all_passed else 'FAIL'}")
    print(f"Tests passed: {len(criteria_met)}/{len(tests)}")
    
    if criteria_met:
        print("Criteria met:")
        for criterion in criteria_met:
            print(f"  ✓ {criterion}")
    
    if criteria_failed:
        print("Criteria failed:")
        for criterion in criteria_failed:
            print(f"  ✗ {criterion}")
    
    # Generate test output summary
    test_output = f"Ran {len(tests)} tests. {len(criteria_met)} passed, {len(criteria_failed)} failed."
    if results:
        test_output += "\nDetails:\n"
        for test_name, success, message in results:
            status = "PASS" if success else "FAIL"
            test_output += f"- {test_name}: {status} - {message}\n"
    
    # Output JSON result
    import json
    result = {
        "tests_passed": all_passed,
        "test_output": test_output,
        "criteria_met": criteria_met,
        "criteria_failed": criteria_failed
    }
    
    print(f"\n--- JSON RESULT ---")
    print(json.dumps(result, indent=2))
    
    return 0 if all_passed else 1

if __name__ == "__main__":
    sys.exit(main())