#!/usr/bin/env python3
"""
Test script to verify Spring Boot 3.x LTS upgrade acceptance criteria
"""

import xml.etree.ElementTree as ET
import sys
import re

def test_spring_boot_3x_lts_version():
    """Verify parent POM is updated to Spring Boot 3.x LTS version"""
    try:
        tree = ET.parse('pom.xml')
        root = tree.getroot()
        
        # Find parent section
        parent = root.find('.//{http://maven.apache.org/POM/4.0.0}parent')
        if parent is None:
            return False, "No parent section found in POM"
        
        # Check groupId is Spring Boot
        group_id = parent.find('.//{http://maven.apache.org/POM/4.0.0}groupId')
        if group_id is None or group_id.text != 'org.springframework.boot':
            return False, f"Parent groupId is not Spring Boot: {group_id.text if group_id is not None else 'None'}"
        
        # Check artifactId is spring-boot-starter-parent
        artifact_id = parent.find('.//{http://maven.apache.org/POM/4.0.0}artifactId')
        if artifact_id is None or artifact_id.text != 'spring-boot-starter-parent':
            return False, f"Parent artifactId is not spring-boot-starter-parent: {artifact_id.text if artifact_id is not None else 'None'}"
        
        # Check version is 3.x LTS
        version = parent.find('.//{http://maven.apache.org/POM/4.0.0}version')
        if version is None:
            return False, "No version found in parent section"
        
        version_text = version.text
        if not version_text.startswith('3.'):
            return False, f"Version is not 3.x: {version_text}"
        
        # Check if it's an LTS version (3.1.x or 3.2.x are LTS)
        if not (version_text.startswith('3.1.') or version_text.startswith('3.2.')):
            return False, f"Version {version_text} is not an LTS version (should be 3.1.x or 3.2.x)"
        
        return True, f"Spring Boot parent version is {version_text} (3.x LTS)"
        
    except Exception as e:
        return False, f"Error parsing POM: {str(e)}"

def test_java_version_17_or_later():
    """Verify Java version property is updated to 17 or later"""
    try:
        tree = ET.parse('pom.xml')
        root = tree.getroot()
        
        # Find properties section
        properties = root.find('.//{http://maven.apache.org/POM/4.0.0}properties')
        if properties is None:
            return False, "No properties section found in POM"
        
        # Check java.version property
        java_version = properties.find('.//{http://maven.apache.org/POM/4.0.0}java.version')
        if java_version is None:
            return False, "No java.version property found"
        
        version_text = java_version.text
        try:
            version_num = int(version_text)
            if version_num < 17:
                return False, f"Java version {version_text} is less than 17"
            return True, f"Java version is {version_text} (17 or later)"
        except ValueError:
            return False, f"Invalid Java version format: {version_text}"
        
    except Exception as e:
        return False, f"Error parsing POM: {str(e)}"

def test_maven_compiler_plugin_java_17():
    """Verify Maven compiler plugin is configured for Java 17 target"""
    try:
        tree = ET.parse('pom.xml')
        root = tree.getroot()
        
        # Find build/plugins section
        plugins = root.findall('.//{http://maven.apache.org/POM/4.0.0}plugin')
        
        compiler_plugin = None
        for plugin in plugins:
            artifact_id = plugin.find('.//{http://maven.apache.org/POM/4.0.0}artifactId')
            if artifact_id is not None and artifact_id.text == 'maven-compiler-plugin':
                compiler_plugin = plugin
                break
        
        if compiler_plugin is None:
            return False, "Maven compiler plugin not found"
        
        # Check configuration
        configuration = compiler_plugin.find('.//{http://maven.apache.org/POM/4.0.0}configuration')
        if configuration is None:
            return False, "No configuration found for maven-compiler-plugin"
        
        # Check source version
        source = configuration.find('.//{http://maven.apache.org/POM/4.0.0}source')
        if source is None:
            return False, "No source version configured in maven-compiler-plugin"
        
        # Check target version
        target = configuration.find('.//{http://maven.apache.org/POM/4.0.0}target')
        if target is None:
            return False, "No target version configured in maven-compiler-plugin"
        
        source_version = source.text
        target_version = target.text
        
        try:
            source_num = int(source_version)
            target_num = int(target_version)
            
            if source_num < 17:
                return False, f"Maven compiler source version {source_version} is less than 17"
            if target_num < 17:
                return False, f"Maven compiler target version {target_version} is less than 17"
            
            return True, f"Maven compiler plugin configured for Java {source_version}/{target_version}"
            
        except ValueError:
            return False, f"Invalid compiler version format: source={source_version}, target={target_version}"
        
    except Exception as e:
        return False, f"Error parsing POM: {str(e)}"

def main():
    """Run all tests and report results"""
    tests = [
        ("Parent POM updated to Spring Boot 3.x LTS version", test_spring_boot_3x_lts_version),
        ("Java version property updated to 17 or later", test_java_version_17_or_later),
        ("Maven compiler plugin configured for Java 17 target", test_maven_compiler_plugin_java_17)
    ]
    
    results = []
    all_passed = True
    
    print("=== Spring Boot 3.x LTS Upgrade Verification ===\n")
    
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
    
    return 0 if all_passed else 1

if __name__ == "__main__":
    sys.exit(main())