#!/usr/bin/env python3
"""
TDD tests for Spring Boot parent version upgrade to 2.7.18.
These tests verify the acceptance criteria:
- Spring Boot parent version updated to 2.7.18 in root pom.xml
- All Spring Boot dependencies automatically updated to 2.7.x versions
"""

import xml.etree.ElementTree as ET
import sys
import os

NAMESPACE = {'mvn': 'http://maven.apache.org/POM/4.0.0'}

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

def test_root_pom_spring_boot_parent_version():
    """Test that root pom.xml has Spring Boot parent version 2.7.18"""
    root = parse_pom('pom.xml')
    
    parent_version = get_text(root, 'mvn:parent/mvn:version')
    
    assert parent_version == '2.7.18', (
        f"Expected Spring Boot parent version '2.7.18' but got '{parent_version}'. "
        f"Root pom.xml must be updated to use spring-boot-starter-parent 2.7.18"
    )
    print(f"✓ Root pom.xml Spring Boot parent version is {parent_version}")

def test_root_pom_parent_artifact_id():
    """Test that root pom.xml still uses spring-boot-starter-parent"""
    root = parse_pom('pom.xml')
    
    parent_artifact_id = get_text(root, 'mvn:parent/mvn:artifactId')
    
    assert parent_artifact_id == 'spring-boot-starter-parent', (
        f"Expected parent artifactId 'spring-boot-starter-parent' but got '{parent_artifact_id}'"
    )
    print(f"✓ Root pom.xml parent artifactId is {parent_artifact_id}")

def test_root_pom_parent_group_id():
    """Test that root pom.xml still uses org.springframework.boot group"""
    root = parse_pom('pom.xml')
    
    parent_group_id = get_text(root, 'mvn:parent/mvn:groupId')
    
    assert parent_group_id == 'org.springframework.boot', (
        f"Expected parent groupId 'org.springframework.boot' but got '{parent_group_id}'"
    )
    print(f"✓ Root pom.xml parent groupId is {parent_group_id}")

def test_root_pom_not_old_version():
    """Test that root pom.xml no longer uses the old 2.2.1.RELEASE version"""
    root = parse_pom('pom.xml')
    
    parent_version = get_text(root, 'mvn:parent/mvn:version')
    
    assert parent_version != '2.2.1.RELEASE', (
        f"Root pom.xml still uses old version '2.2.1.RELEASE'. "
        f"Must be updated to '2.7.18'"
    )
    print(f"✓ Root pom.xml no longer uses old version 2.2.1.RELEASE")

def test_root_pom_is_valid_xml():
    """Test that root pom.xml is valid XML after modification"""
    try:
        root = parse_pom('pom.xml')
        assert root is not None
        print("✓ Root pom.xml is valid XML")
    except ET.ParseError as e:
        assert False, f"Root pom.xml is not valid XML: {e}"

def test_root_pom_preserves_project_structure():
    """Test that root pom.xml preserves essential project structure"""
    root = parse_pom('pom.xml')
    
    # Check groupId
    group_id = get_text(root, 'mvn:groupId')
    assert group_id == 'org.monolithic.petclinic', (
        f"Expected groupId 'org.monolithic.petclinic' but got '{group_id}'"
    )
    
    # Check artifactId
    artifact_id = get_text(root, 'mvn:artifactId')
    assert artifact_id == 'petclinic-root', (
        f"Expected artifactId 'petclinic-root' but got '{artifact_id}'"
    )
    
    # Check packaging
    packaging = get_text(root, 'mvn:packaging')
    assert packaging == 'pom', (
        f"Expected packaging 'pom' but got '{packaging}'"
    )
    
    print(f"✓ Root pom.xml preserves project structure (groupId={group_id}, artifactId={artifact_id})")

def test_root_pom_preserves_modules():
    """Test that root pom.xml preserves module declarations"""
    root = parse_pom('pom.xml')
    
    modules_element = root.find('mvn:modules', NAMESPACE)
    assert modules_element is not None, "modules element not found in root pom.xml"
    
    modules = [m.text for m in modules_element.findall('mvn:module', NAMESPACE)]
    assert 'activemq' in modules, f"'activemq' module not found in modules: {modules}"
    assert 'monolith' in modules, f"'monolith' module not found in modules: {modules}"
    
    print(f"✓ Root pom.xml preserves modules: {modules}")

def test_root_pom_version_is_2_7_x():
    """Test that the version is in the 2.7.x range"""
    root = parse_pom('pom.xml')
    
    parent_version = get_text(root, 'mvn:parent/mvn:version')
    
    assert parent_version is not None, "Parent version not found"
    assert parent_version.startswith('2.7.'), (
        f"Expected version to start with '2.7.' but got '{parent_version}'. "
        f"Version must be in the 2.7.x range"
    )
    print(f"✓ Root pom.xml version {parent_version} is in the 2.7.x range")

def run_all_tests():
    """Run all tests and report results."""
    tests = [
        test_root_pom_is_valid_xml,
        test_root_pom_parent_group_id,
        test_root_pom_parent_artifact_id,
        test_root_pom_spring_boot_parent_version,
        test_root_pom_not_old_version,
        test_root_pom_version_is_2_7_x,
        test_root_pom_preserves_project_structure,
        test_root_pom_preserves_modules,
    ]
    
    passed = 0
    failed = 0
    failures = []
    
    print("=" * 60)
    print("Running Spring Boot version upgrade tests")
    print("=" * 60)
    
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
    
    print("=" * 60)
    print(f"Results: {passed} passed, {failed} failed")
    
    if failures:
        print("\nFailed tests:")
        for name, msg in failures:
            print(f"  - {name}: {msg}")
        sys.exit(1)
    else:
        print("\nAll tests passed! ✓")
        sys.exit(0)

if __name__ == '__main__':
    # Change to the directory containing pom.xml if needed
    script_dir = os.path.dirname(os.path.abspath(__file__))
    os.chdir(script_dir)
    run_all_tests()
