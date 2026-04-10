#!/usr/bin/env python3
"""
Test script to verify Java 17 Maven configuration acceptance criteria
"""
import subprocess
import sys
import xml.etree.ElementTree as ET
import os

def test_java_version_property():
    """Test that java.version property is set to 17 in pom.xml"""
    print("Testing java.version property in pom.xml...")
    
    try:
        tree = ET.parse('pom.xml')
        root = tree.getroot()
        
        # Find the properties section
        ns = {'maven': 'http://maven.apache.org/POM/4.0.0'}
        properties = root.find('.//maven:properties', ns)
        
        if properties is None:
            # Try without namespace
            properties = root.find('.//properties')
        
        if properties is None:
            print("❌ FAIL: No properties section found in pom.xml")
            return False
        
        java_version = properties.find('.//java.version')
        if java_version is None:
            # Try with namespace
            java_version = properties.find('.//maven:java.version', ns)
        
        if java_version is None:
            print("❌ FAIL: java.version property not found in pom.xml")
            return False
        
        if java_version.text == '17':
            print("✅ PASS: java.version property is set to 17")
            return True
        else:
            print(f"❌ FAIL: java.version property is set to '{java_version.text}', expected '17'")
            return False
            
    except Exception as e:
        print(f"❌ FAIL: Error parsing pom.xml: {e}")
        return False

def test_maven_compiler_plugin_config():
    """Test that Maven compiler plugin is configured for Java 17"""
    print("Testing Maven compiler plugin configuration...")
    
    # Since Spring Boot parent manages the compiler plugin, we need to check
    # if the java.version property is properly inherited
    # We'll verify this by attempting to compile the project
    
    try:
        # Check if the effective POM shows Java 17 configuration
        result = subprocess.run(['./mvnw', 'help:effective-pom', '-q'], 
                              capture_output=True, text=True, timeout=60)
        
        if result.returncode != 0:
            print(f"❌ FAIL: Could not get effective POM: {result.stderr}")
            return False
        
        effective_pom = result.stdout
        
        # Check for Java 17 in the effective POM
        if '<source>17</source>' in effective_pom and '<target>17</target>' in effective_pom:
            print("✅ PASS: Maven compiler plugin configured for Java 17 source and target")
            return True
        elif 'java.version>17<' in effective_pom:
            print("✅ PASS: Java 17 configuration found via java.version property")
            return True
        else:
            print("❌ FAIL: Maven compiler plugin not configured for Java 17")
            print("Effective POM snippet:")
            # Show relevant parts of effective POM
            lines = effective_pom.split('\n')
            for i, line in enumerate(lines):
                if 'maven-compiler-plugin' in line:
                    for j in range(max(0, i-5), min(len(lines), i+20)):
                        print(f"  {lines[j]}")
                    break
            return False
            
    except subprocess.TimeoutExpired:
        print("❌ FAIL: Maven command timed out")
        return False
    except Exception as e:
        print(f"❌ FAIL: Error checking Maven compiler plugin: {e}")
        return False

def test_project_compilation():
    """Test that the project compiles successfully with Java 17"""
    print("Testing project compilation...")
    
    try:
        # Clean and compile the project
        result = subprocess.run(['./mvnw', 'clean', 'compile', '-q'], 
                              capture_output=True, text=True, timeout=120)
        
        if result.returncode == 0:
            print("✅ PASS: Project compiles successfully")
            return True
        else:
            print(f"❌ FAIL: Project compilation failed")
            print(f"Error output: {result.stderr}")
            print(f"Standard output: {result.stdout}")
            return False
            
    except subprocess.TimeoutExpired:
        print("❌ FAIL: Maven compilation timed out")
        return False
    except Exception as e:
        print(f"❌ FAIL: Error during compilation: {e}")
        return False

def main():
    """Run all tests and report results"""
    print("=== Java 17 Maven Configuration Verification ===\n")
    
    tests = [
        ("java.version property updated to 17 in pom.xml", test_java_version_property),
        ("Maven compiler plugin configured for Java 17 source and target", test_maven_compiler_plugin_config),
        ("Project compiles successfully with Java 17", test_project_compilation)
    ]
    
    passed_tests = []
    failed_tests = []
    
    for test_name, test_func in tests:
        print(f"\n--- {test_name} ---")
        try:
            if test_func():
                passed_tests.append(test_name)
            else:
                failed_tests.append(test_name)
        except Exception as e:
            print(f"❌ FAIL: Test error: {e}")
            failed_tests.append(test_name)
    
    print(f"\n=== Test Results ===")
    print(f"Passed: {len(passed_tests)}")
    print(f"Failed: {len(failed_tests)}")
    
    if passed_tests:
        print(f"\n✅ Passed tests:")
        for test in passed_tests:
            print(f"  - {test}")
    
    if failed_tests:
        print(f"\n❌ Failed tests:")
        for test in failed_tests:
            print(f"  - {test}")
    
    return len(failed_tests) == 0, passed_tests, failed_tests

if __name__ == "__main__":
    success, passed, failed = main()
    sys.exit(0 if success else 1)