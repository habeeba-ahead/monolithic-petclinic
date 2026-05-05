#!/usr/bin/env python3
"""
Complete verification of Spring Boot 3.x LTS upgrade including child modules
"""

import xml.etree.ElementTree as ET
import sys
import os

def check_child_module_inheritance(module_path):
    """Check that child module properly inherits from updated parent"""
    try:
        pom_path = os.path.join(module_path, 'pom.xml')
        if not os.path.exists(pom_path):
            return False, f"POM not found at {pom_path}"
        
        tree = ET.parse(pom_path)
        root = tree.getroot()
        
        # Find parent section
        parent = root.find('.//{http://maven.apache.org/POM/4.0.0}parent')
        if parent is None:
            return False, f"No parent section found in {module_path}/pom.xml"
        
        # Check parent groupId and artifactId
        group_id = parent.find('.//{http://maven.apache.org/POM/4.0.0}groupId')
        artifact_id = parent.find('.//{http://maven.apache.org/POM/4.0.0}artifactId')
        version = parent.find('.//{http://maven.apache.org/POM/4.0.0}version')
        
        if group_id is None or group_id.text != 'org.monolithic.petclinic':
            return False, f"Child module {module_path} has incorrect parent groupId: {group_id.text if group_id is not None else 'None'}"
        
        if artifact_id is None or artifact_id.text != 'petclinic-root':
            return False, f"Child module {module_path} has incorrect parent artifactId: {artifact_id.text if artifact_id is not None else 'None'}"
        
        if version is None or version.text != '1.0.0-SNAPSHOT':
            return False, f"Child module {module_path} has incorrect parent version: {version.text if version is not None else 'None'}"
        
        return True, f"Child module {module_path} correctly inherits from updated parent"
        
    except Exception as e:
        return False, f"Error checking child module {module_path}: {str(e)}"

def run_comprehensive_tests():
    """Run all tests including child module inheritance"""
    
    # Import the original test functions
    exec(open('verify-spring-boot-upgrade.py').read(), globals())
    
    tests = [
        ("Parent POM updated to Spring Boot 3.x LTS version", test_spring_boot_3x_lts_version),
        ("Java version property updated to 17 or later", test_java_version_17_or_later),
        ("Maven compiler plugin configured for Java 17 target", test_maven_compiler_plugin_java_17),
        ("ActiveMQ module inherits from updated parent", lambda: check_child_module_inheritance('activemq')),
        ("Monolith module inherits from updated parent", lambda: check_child_module_inheritance('monolith'))
    ]
    
    results = []
    all_passed = True
    
    print("=== Complete Spring Boot 3.x LTS Upgrade Verification ===\n")
    
    for test_name, test_func in tests:
        try:
            passed, message = test_func()
            results.append((test_name, passed, message))
            status = "PASS" if passed else "FAIL"
            print(f"[{status}] {test_name}")
            print(f"       {message}\n")
            
            if not passed:
                all_passed = False
                
        except Exception as e:
            results.append((test_name, False, f"Test error: {str(e)}"))
            print(f"[ERROR] {test_name}")
            print(f"        Test error: {str(e)}\n")
            all_passed = False
    
    print("=== Summary ===")
    print(f"Overall result: {'PASS' if all_passed else 'FAIL'}")
    print(f"Tests passed: {sum(1 for _, passed, _ in results if passed)}/{len(results)}")
    
    return all_passed, results

if __name__ == "__main__":
    passed, results = run_comprehensive_tests()
    sys.exit(0 if passed else 1)