#!/usr/bin/env python3
"""
Test script to verify Java 17 upgrade in Maven configuration
"""

import xml.etree.ElementTree as ET
import subprocess
import sys
import os

def test_java_version_property():
    """Test that java.version property is set to 17"""
    print("Testing java.version property...")
    
    tree = ET.parse('pom.xml')
    root = tree.getroot()
    
    # Find the java.version property
    ns = {'maven': 'http://maven.apache.org/POM/4.0.0'}
    java_version = None
    
    for properties in root.findall('.//maven:properties', ns):
        for prop in properties:
            if prop.tag.endswith('java.version'):
                java_version = prop.text
                break
    
    if java_version == '17':
        print("✓ java.version property is correctly set to 17")
        return True
    else:
        print(f"✗ java.version property is {java_version}, expected 17")
        return False

def test_maven_compiler_plugin():
    """Test that Maven compiler plugin is configured for Java 17"""
    print("Testing Maven compiler plugin configuration...")
    
    tree = ET.parse('pom.xml')
    root = tree.getroot()
    
    ns = {'maven': 'http://maven.apache.org/POM/4.0.0'}
    
    # Find the maven-compiler-plugin
    compiler_plugin = None
    for plugin in root.findall('.//maven:plugin', ns):
        artifact_id = plugin.find('maven:artifactId', ns)
        if artifact_id is not None and artifact_id.text == 'maven-compiler-plugin':
            compiler_plugin = plugin
            break
    
    if compiler_plugin is None:
        print("✗ Maven compiler plugin not found")
        return False
    
    # Check source and target versions
    config = compiler_plugin.find('maven:configuration', ns)
    if config is None:
        print("✗ Maven compiler plugin configuration not found")
        return False
    
    source = config.find('maven:source', ns)
    target = config.find('maven:target', ns)
    
    source_version = source.text if source is not None else None
    target_version = target.text if target is not None else None
    
    if source_version == '17' and target_version == '17':
        print("✓ Maven compiler plugin configured for Java 17 (source: 17, target: 17)")
        return True
    else:
        print(f"✗ Maven compiler plugin misconfigured (source: {source_version}, target: {target_version})")
        return False

def test_project_compilation():
    """Test that the project compiles successfully with Java 17"""
    print("Testing project compilation...")
    
    try:
        # Check if Java 17 is available
        java_result = subprocess.run(['java', '-version'], 
                                   capture_output=True, text=True, timeout=30)
        print(f"Java version check output: {java_result.stderr}")
        
        # Clean and compile the project using Maven wrapper
        print("Running: ./mvnw clean compile")
        result = subprocess.run(['./mvnw', 'clean', 'compile'], 
                              capture_output=True, text=True, timeout=600)
        
        if result.returncode == 0:
            print("✓ Project compiles successfully with Java 17")
            return True
        else:
            print("✗ Project compilation failed")
            print("STDOUT:", result.stdout[-1000:])  # Last 1000 chars to avoid too much output
            print("STDERR:", result.stderr[-1000:])
            return False
    except subprocess.TimeoutExpired:
        print("✗ Project compilation timed out")
        return False
    except Exception as e:
        print(f"✗ Error during compilation: {e}")
        return False

def main():
    """Run all tests and report results"""
    print("Verifying Java 17 upgrade in Maven configuration...\n")
    
    tests = [
        ("java.version property set to 17", test_java_version_property),
        ("Maven compiler plugin configured for Java 17", test_maven_compiler_plugin),
        ("Project compiles successfully with Java 17", test_project_compilation)
    ]
    
    results = []
    passed_criteria = []
    failed_criteria = []
    
    for test_name, test_func in tests:
        print(f"\n--- {test_name} ---")
        try:
            result = test_func()
            results.append(result)
            if result:
                passed_criteria.append(test_name)
            else:
                failed_criteria.append(test_name)
        except Exception as e:
            print(f"✗ Test failed with exception: {e}")
            results.append(False)
            failed_criteria.append(test_name)
    
    all_passed = all(results)
    
    print(f"\n{'='*60}")
    print("VERIFICATION SUMMARY")
    print(f"{'='*60}")
    print(f"Tests passed: {sum(results)}/{len(results)}")
    print(f"Overall result: {'PASS' if all_passed else 'FAIL'}")
    
    if passed_criteria:
        print(f"\nPassed criteria:")
        for criterion in passed_criteria:
            print(f"  ✓ {criterion}")
    
    if failed_criteria:
        print(f"\nFailed criteria:")
        for criterion in failed_criteria:
            print(f"  ✗ {criterion}")
    
    return all_passed, passed_criteria, failed_criteria

if __name__ == "__main__":
    success, passed, failed = main()
    sys.exit(0 if success else 1)