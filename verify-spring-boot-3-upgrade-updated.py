#!/usr/bin/env python3
"""
Test script to verify Spring Boot 3.x upgrade acceptance criteria
"""
import subprocess
import sys
import xml.etree.ElementTree as ET
import re

def test_pom_spring_boot_version():
    """Test that root POM has Spring Boot 3.x parent version"""
    print("Testing root POM Spring Boot version...")
    
    try:
        tree = ET.parse('pom.xml')
        root = tree.getroot()
        
        # Find parent version
        parent = root.find('.//{http://maven.apache.org/POM/4.0.0}parent')
        if parent is None:
            print("❌ No parent found in root POM")
            return False
            
        group_id = parent.find('.//{http://maven.apache.org/POM/4.0.0}groupId')
        artifact_id = parent.find('.//{http://maven.apache.org/POM/4.0.0}artifactId')
        version = parent.find('.//{http://maven.apache.org/POM/4.0.0}version')
        
        if group_id is None or artifact_id is None or version is None:
            print("❌ Missing parent information in root POM")
            return False
            
        if (group_id.text == 'org.springframework.boot' and 
            artifact_id.text == 'spring-boot-starter-parent' and
            version.text.startswith('3.')):
            print(f"✅ Root POM has Spring Boot 3.x parent: {version.text}")
            return True
        else:
            print(f"❌ Root POM parent is not Spring Boot 3.x: {group_id.text}:{artifact_id.text}:{version.text}")
            return False
            
    except Exception as e:
        print(f"❌ Error reading root POM: {e}")
        return False

def test_monolith_pom_parent():
    """Test that monolith POM inherits from correct parent"""
    print("Testing monolith POM parent...")
    
    try:
        tree = ET.parse('monolith/pom.xml')
        root = tree.getroot()
        
        parent = root.find('.//{http://maven.apache.org/POM/4.0.0}parent')
        if parent is None:
            print("❌ No parent found in monolith POM")
            return False
            
        group_id = parent.find('.//{http://maven.apache.org/POM/4.0.0}groupId')
        artifact_id = parent.find('.//{http://maven.apache.org/POM/4.0.0}artifactId')
        
        if (group_id is not None and artifact_id is not None and
            group_id.text == 'org.monolithic.petclinic' and
            artifact_id.text == 'petclinic-root'):
            print("✅ Monolith POM inherits from petclinic-root (which has Spring Boot 3.x)")
            return True
        else:
            print(f"❌ Monolith POM has incorrect parent: {group_id.text if group_id is not None else 'None'}:{artifact_id.text if artifact_id is not None else 'None'}")
            return False
            
    except Exception as e:
        print(f"❌ Error reading monolith POM: {e}")
        return False

def test_activemq_pom_parent():
    """Test that activemq POM inherits from correct parent"""
    print("Testing activemq POM parent...")
    
    try:
        tree = ET.parse('activemq/pom.xml')
        root = tree.getroot()
        
        parent = root.find('.//{http://maven.apache.org/POM/4.0.0}parent')
        if parent is None:
            print("❌ No parent found in activemq POM")
            return False
            
        group_id = parent.find('.//{http://maven.apache.org/POM/4.0.0}groupId')
        artifact_id = parent.find('.//{http://maven.apache.org/POM/4.0.0}artifactId')
        
        if (group_id is not None and artifact_id is not None and
            group_id.text == 'org.monolithic.petclinic' and
            artifact_id.text == 'petclinic-root'):
            print("✅ ActiveMQ POM inherits from petclinic-root (which has Spring Boot 3.x)")
            return True
        else:
            print(f"❌ ActiveMQ POM has incorrect parent: {group_id.text if group_id is not None else 'None'}:{artifact_id.text if artifact_id is not None else 'None'}")
            return False
            
    except Exception as e:
        print(f"❌ Error reading activemq POM: {e}")
        return False

def test_maven_dependency_resolution():
    """Test that Maven can resolve all dependencies without conflicts"""
    print("Testing Maven dependency resolution...")
    
    try:
        # Run Maven dependency:resolve to check if all dependencies can be resolved
        result = subprocess.run(['mvn', 'dependency:resolve', '-q'], 
                              capture_output=True, text=True, timeout=300)
        
        if result.returncode == 0:
            print("✅ Maven can resolve all dependencies")
            return True, result.stdout + result.stderr
        else:
            print(f"❌ Maven dependency resolution failed with return code {result.returncode}")
            print(f"Error output: {result.stderr}")
            return False, result.stdout + result.stderr
            
    except subprocess.TimeoutExpired:
        print("❌ Maven dependency resolution timed out")
        return False, "Timeout"
    except Exception as e:
        print(f"❌ Error running Maven dependency resolution: {e}")
        return False, str(e)

def test_dependency_conflicts():
    """Test for problematic dependency version conflicts"""
    print("Testing for problematic dependency conflicts...")
    
    try:
        # Run Maven dependency:tree to check for conflicts
        result = subprocess.run(['mvn', 'dependency:tree', '-Dverbose'], 
                              capture_output=True, text=True, timeout=300)
        
        output = result.stdout + result.stderr
        
        # Look for problematic conflict indicators (not just "omitted for conflict" which is normal)
        problematic_patterns = [
            r'Failed to collect dependencies',
            r'Could not resolve dependencies',
            r'version conflict.*failed',
            r'Dependency convergence error'
        ]
        
        conflicts_found = []
        for pattern in problematic_patterns:
            matches = re.findall(pattern, output, re.IGNORECASE)
            if matches:
                conflicts_found.extend(matches)
        
        # Check if Maven command succeeded and no problematic conflicts found
        if result.returncode == 0 and not conflicts_found:
            # Count normal version resolution messages (these are expected)
            normal_conflicts = len(re.findall(r'omitted for conflict', output, re.IGNORECASE))
            print(f"✅ No problematic dependency conflicts detected ({normal_conflicts} normal version resolutions found)")
            return True, output
        else:
            if conflicts_found:
                print(f"❌ Problematic dependency conflicts detected: {conflicts_found}")
            if result.returncode != 0:
                print(f"❌ Maven dependency tree command failed with return code {result.returncode}")
            return False, output
            
    except subprocess.TimeoutExpired:
        print("❌ Maven dependency tree analysis timed out")
        return False, "Timeout"
    except Exception as e:
        print(f"❌ Error running Maven dependency tree: {e}")
        return False, str(e)

def main():
    """Run all tests and report results"""
    print("=== Spring Boot 3.x Upgrade Verification ===\n")
    
    tests_passed = []
    tests_failed = []
    test_outputs = []
    
    # Test 1: Root POM Spring Boot version
    if test_pom_spring_boot_version():
        tests_passed.append("Root POM updated to Spring Boot 3.x")
    else:
        tests_failed.append("Root POM not updated to Spring Boot 3.x")
    
    print()
    
    # Test 2: Monolith POM parent
    if test_monolith_pom_parent():
        tests_passed.append("Monolith POM inherits Spring Boot 3.x via parent")
    else:
        tests_failed.append("Monolith POM parent configuration incorrect")
    
    print()
    
    # Test 3: ActiveMQ POM parent
    if test_activemq_pom_parent():
        tests_passed.append("ActiveMQ POM inherits Spring Boot 3.x via parent")
    else:
        tests_failed.append("ActiveMQ POM parent configuration incorrect")
    
    print()
    
    # Test 4: Maven dependency resolution
    dep_resolve_success, dep_output = test_maven_dependency_resolution()
    test_outputs.append(f"Dependency resolution: {'SUCCESS' if dep_resolve_success else 'FAILED'}")
    if dep_resolve_success:
        tests_passed.append("Maven can resolve all dependencies")
    else:
        tests_failed.append("Maven dependency resolution failed")
    
    print()
    
    # Test 5: Dependency conflicts
    conflict_success, conflict_output = test_dependency_conflicts()
    test_outputs.append(f"Dependency conflicts check: {'PASSED' if conflict_success else 'FAILED'}")
    if conflict_success:
        tests_passed.append("No problematic dependency version conflicts")
    else:
        tests_failed.append("Problematic dependency version conflicts detected")
    
    print()
    
    # Summary
    all_passed = len(tests_failed) == 0
    print("=== Test Summary ===")
    print(f"Tests passed: {len(tests_passed)}")
    print(f"Tests failed: {len(tests_failed)}")
    
    if tests_passed:
        print("\n✅ Passed criteria:")
        for test in tests_passed:
            print(f"  - {test}")
    
    if tests_failed:
        print("\n❌ Failed criteria:")
        for test in tests_failed:
            print(f"  - {test}")
    
    # Output JSON result
    import json
    result = {
        "tests_passed": all_passed,
        "test_output": " | ".join(test_outputs),
        "criteria_met": tests_passed,
        "criteria_failed": tests_failed
    }
    
    print(f"\n=== JSON Result ===")
    print(json.dumps(result, indent=2))
    
    return 0 if all_passed else 1

if __name__ == "__main__":
    sys.exit(main())