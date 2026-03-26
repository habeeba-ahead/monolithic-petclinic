#!/usr/bin/env python3
"""
Test script to verify Java 17 Maven configuration
"""
import xml.etree.ElementTree as ET
import subprocess
import sys
import os

def test_java_version_property():
    """Test that java.version property is set to 17 in pom.xml"""
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
        print("✗ maven-compiler-plugin not found")
        return False
    
    # Check source and target configuration
    config = compiler_plugin.find('maven:configuration', ns)
    if config is None:
        print("✗ maven-compiler-plugin configuration not found")
        return False
    
    source = config.find('maven:source', ns)
    target = config.find('maven:target', ns)
    
    source_ok = source is not None and source.text == '17'
    target_ok = target is not None and target.text == '17'
    
    if source_ok and target_ok:
        print("✓ Maven compiler plugin source and target are correctly set to 17")
        return True
    else:
        print(f"✗ Maven compiler plugin configuration incorrect:")
        print(f"  Source: {source.text if source is not None else 'not found'}")
        print(f"  Target: {target.text if target is not None else 'not found'}")
        return False

def test_project_compilation():
    """Test that the project compiles successfully with Java 17"""
    print("Testing project compilation...")
    
    try:
        # Run maven compile
        result = subprocess.run(['mvn', 'clean', 'compile', '-q'], 
                              capture_output=True, text=True, timeout=300)
        
        if result.returncode == 0:
            print("✓ Project compiles successfully with Java 17")
            return True
        else:
            print("✗ Project compilation failed:")
            print("STDOUT:", result.stdout)
            print("STDERR:", result.stderr)
            return False
    except subprocess.TimeoutExpired:
        print("✗ Compilation timed out")
        return False
    except FileNotFoundError:
        print("✗ Maven not found - cannot test compilation")
        return False
    except Exception as e:
        print(f"✗ Compilation test failed with error: {e}")
        return False

def main():
    """Run all tests"""
    print("Verifying Java 17 Maven configuration...\n")
    
    tests = [
        ("java.version property updated to 17 in pom.xml", test_java_version_property),
        ("Maven compiler plugin configured for Java 17 source and target", test_maven_compiler_plugin),
        ("Project compiles successfully with Java 17", test_project_compilation)
    ]
    
    results = []
    for test_name, test_func in tests:
        print(f"\n--- {test_name} ---")
        try:
            result = test_func()
            results.append((test_name, result))
        except Exception as e:
            print(f"✗ Test failed with exception: {e}")
            results.append((test_name, False))
    
    print("\n" + "="*60)
    print("SUMMARY:")
    print("="*60)
    
    passed = []
    failed = []
    
    for test_name, result in results:
        if result:
            print(f"✓ {test_name}")
            passed.append(test_name)
        else:
            print(f"✗ {test_name}")
            failed.append(test_name)
    
    print(f"\nTests passed: {len(passed)}/{len(results)}")
    
    return len(failed) == 0, passed, failed

if __name__ == "__main__":
    success, passed, failed = main()
    sys.exit(0 if success else 1)