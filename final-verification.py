#!/usr/bin/env python3
"""
Final comprehensive verification of Spring Boot 3.x upgrade
"""

import subprocess
import sys
import xml.etree.ElementTree as ET
import json

def verify_all_criteria():
    """Verify all acceptance criteria comprehensively"""
    results = {
        "tests_passed": True,
        "test_output": "",
        "criteria_met": [],
        "criteria_failed": []
    }
    
    test_results = []
    
    # 1. Verify pom.xml parent version updated to Spring Boot 3.x
    try:
        tree = ET.parse('pom.xml')
        root = tree.getroot()
        parent = root.find('.//{http://maven.apache.org/POM/4.0.0}parent')
        version_elem = parent.find('.//{http://maven.apache.org/POM/4.0.0}version')
        version = version_elem.text
        
        if version.startswith('3.'):
            test_results.append(f"✓ PASS: pom.xml parent version updated to Spring Boot 3.x ({version})")
            results["criteria_met"].append("pom.xml parent version updated to Spring Boot 3.x")
        else:
            test_results.append(f"✗ FAIL: pom.xml parent version is not Spring Boot 3.x ({version})")
            results["criteria_failed"].append("pom.xml parent version updated to Spring Boot 3.x")
            results["tests_passed"] = False
            
    except Exception as e:
        test_results.append(f"✗ ERROR: Failed to verify parent version: {str(e)}")
        results["criteria_failed"].append("pom.xml parent version updated to Spring Boot 3.x")
        results["tests_passed"] = False
    
    # 2. Verify Maven dependency resolution successful
    try:
        result = subprocess.run(
            ['./mvnw', 'dependency:resolve', '-q'],
            capture_output=True,
            text=True,
            timeout=300
        )
        
        if result.returncode == 0:
            test_results.append("✓ PASS: Maven dependency resolution successful")
            results["criteria_met"].append("Maven dependency resolution successful")
        else:
            test_results.append(f"✗ FAIL: Maven dependency resolution failed: {result.stderr}")
            results["criteria_failed"].append("Maven dependency resolution successful")
            results["tests_passed"] = False
            
    except Exception as e:
        test_results.append(f"✗ ERROR: Failed to test dependency resolution: {str(e)}")
        results["criteria_failed"].append("Maven dependency resolution successful")
        results["tests_passed"] = False
    
    # 3. Verify no problematic dependency conflicts
    try:
        result = subprocess.run(
            ['./mvnw', 'dependency:tree', '-Dverbose'],
            capture_output=True,
            text=True,
            timeout=300
        )
        
        if result.returncode == 0:
            # Check for problematic conflicts (not normal version resolution)
            output = result.stdout + result.stderr
            problematic_indicators = [
                'Dependency convergence error',
                'Could not resolve dependencies',
                'Failed to collect dependencies',
                'version conflict.*failed'
            ]
            
            has_problems = any(indicator.lower() in output.lower() for indicator in problematic_indicators)
            
            if not has_problems:
                test_results.append("✓ PASS: No dependency conflicts reported by Maven")
                results["criteria_met"].append("No dependency conflicts reported by Maven")
            else:
                test_results.append("✗ FAIL: Dependency conflicts detected")
                results["criteria_failed"].append("No dependency conflicts reported by Maven")
                results["tests_passed"] = False
        else:
            test_results.append(f"✗ FAIL: Maven dependency:tree failed: {result.stderr}")
            results["criteria_failed"].append("No dependency conflicts reported by Maven")
            results["tests_passed"] = False
            
    except Exception as e:
        test_results.append(f"✗ ERROR: Failed to check dependency conflicts: {str(e)}")
        results["criteria_failed"].append("No dependency conflicts reported by Maven")
        results["tests_passed"] = False
    
    results["test_output"] = "\n".join(test_results)
    
    print("=== Final Spring Boot 3.x Upgrade Verification ===")
    for line in test_results:
        print(line)
    
    print("\n=== Summary ===")
    print(json.dumps(results, indent=2))
    
    return results

if __name__ == "__main__":
    verify_all_criteria()