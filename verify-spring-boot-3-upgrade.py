#!/usr/bin/env python3
"""
Test script to verify Spring Boot 3.x upgrade acceptance criteria
"""

import subprocess
import sys
import xml.etree.ElementTree as ET
import re
import json
import os

def test_pom_parent_version():
    """Test that pom.xml parent version is updated to Spring Boot 3.x"""
    try:
        tree = ET.parse('pom.xml')
        root = tree.getroot()
        
        # Find parent version
        parent = root.find('.//{http://maven.apache.org/POM/4.0.0}parent')
        if parent is None:
            return False, "No parent element found in pom.xml"
        
        version_elem = parent.find('.//{http://maven.apache.org/POM/4.0.0}version')
        if version_elem is None:
            return False, "No version element found in parent"
        
        version = version_elem.text
        print(f"Found parent version: {version}")
        
        # Check if version starts with 3.
        if version.startswith('3.'):
            return True, f"Parent version is Spring Boot 3.x: {version}"
        else:
            return False, f"Parent version is not Spring Boot 3.x: {version}"
            
    except Exception as e:
        return False, f"Error parsing pom.xml: {str(e)}"

def get_maven_command():
    """Get the appropriate Maven command (mvnw if available, otherwise mvn)"""
    if os.path.exists('./mvnw'):
        return './mvnw'
    else:
        return 'mvn'

def test_maven_dependency_resolution():
    """Test that Maven dependency resolution is successful"""
    try:
        maven_cmd = get_maven_command()
        print(f"Using Maven command: {maven_cmd}")
        
        # Run mvn dependency:resolve to check if all dependencies can be resolved
        result = subprocess.run(
            [maven_cmd, 'dependency:resolve', '-q'],
            capture_output=True,
            text=True,
            timeout=600  # 10 minute timeout for first run (downloads dependencies)
        )
        
        if result.returncode == 0:
            return True, "Maven dependency resolution successful"
        else:
            return False, f"Maven dependency resolution failed (exit code {result.returncode}): {result.stderr}"
            
    except subprocess.TimeoutExpired:
        return False, "Maven dependency resolution timed out"
    except Exception as e:
        return False, f"Error running Maven: {str(e)}"

def test_no_dependency_conflicts():
    """Test that there are no problematic dependency conflicts reported by Maven"""
    try:
        maven_cmd = get_maven_command()
        
        # Run mvn dependency:tree to check for conflicts
        result = subprocess.run(
            [maven_cmd, 'dependency:tree', '-Dverbose'],
            capture_output=True,
            text=True,
            timeout=300  # 5 minute timeout
        )
        
        if result.returncode != 0:
            return False, f"Maven dependency:tree failed (exit code {result.returncode}): {result.stderr}"
        
        output = result.stdout
        
        # Look for problematic conflict indicators in the output
        # Note: "omitted for conflict" is normal Maven behavior when resolving to newer versions
        # We're looking for actual problematic conflicts
        problematic_patterns = [
            r'Dependency convergence error',
            r'version conflict.*failed',
            r'Could not resolve dependencies',
            r'Failed to collect dependencies'
        ]
        
        conflicts_found = []
        for pattern in problematic_patterns:
            matches = re.findall(pattern, output, re.IGNORECASE)
            if matches:
                conflicts_found.extend(matches)
        
        # Also check stderr for dependency resolution errors
        if result.stderr:
            stderr_conflicts = re.findall(r'dependency.*conflict|resolution.*failed', result.stderr, re.IGNORECASE)
            conflicts_found.extend(stderr_conflicts)
        
        if conflicts_found:
            return False, f"Problematic dependency conflicts found: {conflicts_found}"
        else:
            # Count normal version resolution messages (these are expected and good)
            normal_resolutions = len(re.findall(r'omitted for conflict with', output))
            return True, f"No problematic dependency conflicts detected. Maven successfully resolved {normal_resolutions} version conflicts."
            
    except subprocess.TimeoutExpired:
        return False, "Maven dependency:tree timed out"
    except Exception as e:
        return False, f"Error running Maven dependency:tree: {str(e)}"

def test_spring_boot_3_dependencies_available():
    """Test that Spring Boot 3.x dependencies are available and can be downloaded"""
    try:
        maven_cmd = get_maven_command()
        
        # Run mvn dependency:resolve specifically for Spring Boot dependencies
        result = subprocess.run(
            [maven_cmd, 'dependency:resolve', '-Dincludes=org.springframework.boot:*', '-q'],
            capture_output=True,
            text=True,
            timeout=300
        )
        
        if result.returncode == 0:
            return True, "Spring Boot 3.x dependencies successfully resolved"
        else:
            return False, f"Failed to resolve Spring Boot 3.x dependencies: {result.stderr}"
            
    except subprocess.TimeoutExpired:
        return False, "Spring Boot dependency resolution timed out"
    except Exception as e:
        return False, f"Error resolving Spring Boot dependencies: {str(e)}"

def main():
    """Run all tests and report results"""
    tests = [
        ("pom.xml parent version updated to Spring Boot 3.x", test_pom_parent_version),
        ("Maven dependency resolution successful", test_maven_dependency_resolution),
        ("No dependency conflicts reported by Maven", test_no_dependency_conflicts),
    ]
    
    results = []
    criteria_met = []
    criteria_failed = []
    all_passed = True
    
    print("=== Spring Boot 3.x Upgrade Verification ===\n")
    
    for test_name, test_func in tests:
        print(f"Testing: {test_name}")
        try:
            passed, message = test_func()
            results.append(f"{test_name}: {'PASS' if passed else 'FAIL'} - {message}")
            
            if passed:
                criteria_met.append(test_name)
                print(f"✓ PASS: {message}")
            else:
                criteria_failed.append(test_name)
                all_passed = False
                print(f"✗ FAIL: {message}")
                
        except Exception as e:
            error_msg = f"Test error: {str(e)}"
            results.append(f"{test_name}: ERROR - {error_msg}")
            criteria_failed.append(test_name)
            all_passed = False
            print(f"✗ ERROR: {error_msg}")
        
        print()
    
    # Output summary
    test_output = "\n".join(results)
    
    summary = {
        "tests_passed": all_passed,
        "test_output": test_output,
        "criteria_met": criteria_met,
        "criteria_failed": criteria_failed
    }
    
    print("=== Test Summary ===")
    print(json.dumps(summary, indent=2))
    
    return 0 if all_passed else 1

if __name__ == "__main__":
    sys.exit(main())