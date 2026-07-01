#!/usr/bin/env python3
"""
Verification tests for Spring Boot parent upgrade to 2.7.18.
Tests all acceptance criteria:
1. Spring Boot parent version updated to 2.7.18 in root pom.xml
2. All Spring Boot dependencies automatically updated to 2.7.x versions
3. Project builds successfully with mvn clean compile
"""

import xml.etree.ElementTree as ET
import subprocess
import sys
import os

NAMESPACE = {'mvn': 'http://maven.apache.org/POM/4.0.0'}
REPO_DIR = '/app/output/6d6875db-62c2-42d6-92a6-6b07d9e47432/worker-1/repo'

def parse_pom(path):
    """Parse a pom.xml file and return the root element."""
    tree = ET.parse(path)
    return tree.getroot()

def get_text(element, xpath, ns=NAMESPACE):
    """Get text from an XML element using xpath."""
    found = element.find(xpath, ns)
    if found is not None:
        return found.text
    return None

# ============================================================
# CRITERION 1: Spring Boot parent version updated to 2.7.18
# ============================================================

def test_root_pom_spring_boot_parent_version_is_2_7_18():
    """CRITERION 1: Root pom.xml has Spring Boot parent version 2.7.18"""
    pom_path = os.path.join(REPO_DIR, 'pom.xml')
    root = parse_pom(pom_path)
    
    parent_version = get_text(root, 'mvn:parent/mvn:version')
    parent_artifact_id = get_text(root, 'mvn:parent/mvn:artifactId')
    parent_group_id = get_text(root, 'mvn:parent/mvn:groupId')
    
    assert parent_group_id == 'org.springframework.boot', (
        f"Expected parent groupId 'org.springframework.boot' but got '{parent_group_id}'"
    )
    assert parent_artifact_id == 'spring-boot-starter-parent', (
        f"Expected parent artifactId 'spring-boot-starter-parent' but got '{parent_artifact_id}'"
    )
    assert parent_version == '2.7.18', (
        f"Expected Spring Boot parent version '2.7.18' but got '{parent_version}'. "
        f"Root pom.xml must be updated to use spring-boot-starter-parent 2.7.18"
    )
    print(f"✓ CRITERION 1: Root pom.xml Spring Boot parent version is {parent_version}")

def test_root_pom_not_old_version():
    """CRITERION 1: Root pom.xml no longer uses the old 2.2.1.RELEASE version"""
    pom_path = os.path.join(REPO_DIR, 'pom.xml')
    root = parse_pom(pom_path)
    
    parent_version = get_text(root, 'mvn:parent/mvn:version')
    
    assert parent_version != '2.2.1.RELEASE', (
        f"Root pom.xml still uses old version '2.2.1.RELEASE'. "
        f"Must be updated to '2.7.18'"
    )
    print(f"✓ CRITERION 1: Root pom.xml no longer uses old version 2.2.1.RELEASE")

# ============================================================
# CRITERION 2: All Spring Boot dependencies automatically updated to 2.7.x
# ============================================================

def test_spring_boot_dependencies_are_2_7_x():
    """CRITERION 2: Spring Boot dependencies are automatically at 2.7.x via parent BOM"""
    # Run mvn dependency:tree to check actual resolved versions
    result = subprocess.run(
        ['mvn', 'dependency:tree', '-pl', 'monolith', '-B'],
        capture_output=True, text=True, cwd=REPO_DIR
    )
    output = result.stdout + result.stderr
    
    # Check that spring-boot starters are at 2.7.18
    spring_boot_deps = [
        'spring-boot-starter-data-jpa:jar:2.7.18',
        'spring-boot-starter-activemq:jar:2.7.18',
        'spring-boot-starter-web:jar:2.7.18',
        'spring-boot-starter-thymeleaf:jar:2.7.18',
        'spring-boot-starter-test:jar:2.7.18',
    ]
    
    for dep in spring_boot_deps:
        assert dep in output, (
            f"Expected Spring Boot dependency '{dep}' not found in dependency tree. "
            f"Spring Boot dependencies should be automatically managed at 2.7.x"
        )
        print(f"  ✓ Found: {dep}")
    
    print(f"✓ CRITERION 2: All Spring Boot dependencies are at 2.7.x versions")

def test_spring_boot_activemq_module_dependencies_are_2_7_x():
    """CRITERION 2: Spring Boot dependencies in activemq module are at 2.7.x"""
    result = subprocess.run(
        ['mvn', 'dependency:tree', '-pl', 'activemq', '-B'],
        capture_output=True, text=True, cwd=REPO_DIR
    )
    output = result.stdout + result.stderr
    
    # Check spring-boot-actuator is at 2.7.18
    assert 'spring-boot-actuator:jar:2.7.18' in output, (
        f"Expected 'spring-boot-actuator:jar:2.7.18' in activemq dependency tree. "
        f"Spring Boot dependencies should be automatically managed at 2.7.x"
    )
    print(f"✓ CRITERION 2: Spring Boot actuator in activemq module is at 2.7.18")

# ============================================================
# CRITERION 3: Project builds successfully with mvn clean compile
# ============================================================

def test_project_builds_successfully():
    """CRITERION 3: Project builds successfully with mvn clean compile"""
    result = subprocess.run(
        ['mvn', 'clean', 'compile', '-B'],
        capture_output=True, text=True, cwd=REPO_DIR,
        timeout=300
    )
    output = result.stdout + result.stderr
    
    assert result.returncode == 0, (
        f"mvn clean compile FAILED with return code {result.returncode}.\n"
        f"Build output (last 50 lines):\n" + 
        '\n'.join(output.split('\n')[-50:])
    )
    assert 'BUILD SUCCESS' in output, (
        f"Expected 'BUILD SUCCESS' in Maven output but got:\n" +
        '\n'.join(output.split('\n')[-20:])
    )
    print(f"✓ CRITERION 3: Project builds successfully with mvn clean compile")

def run_all_tests():
    """Run all acceptance criteria tests and report results."""
    tests = [
        # Criterion 1
        test_root_pom_spring_boot_parent_version_is_2_7_18,
        test_root_pom_not_old_version,
        # Criterion 2
        test_spring_boot_dependencies_are_2_7_x,
        test_spring_boot_activemq_module_dependencies_are_2_7_x,
        # Criterion 3
        test_project_builds_successfully,
    ]
    
    passed = 0
    failed = 0
    failures = []
    
    print("=" * 70)
    print("Verifying Acceptance Criteria: Spring Boot Upgrade to 2.7.18")
    print("=" * 70)
    
    for test in tests:
        try:
            test()
            passed += 1
        except AssertionError as e:
            failed += 1
            failures.append((test.__name__, str(e)))
            print(f"✗ {test.__name__}: {e}")
        except Exception as e:
            failed += 1
            failures.append((test.__name__, str(e)))
            print(f"✗ {test.__name__}: Unexpected error: {e}")
    
    print("=" * 70)
    print(f"Results: {passed} passed, {failed} failed")
    
    if failures:
        print("\nFailed tests:")
        for name, msg in failures:
            print(f"  - {name}:")
            print(f"    {msg[:500]}")
        sys.exit(1)
    else:
        print("\nAll acceptance criteria verified! ✓")
        sys.exit(0)

if __name__ == '__main__':
    run_all_tests()
