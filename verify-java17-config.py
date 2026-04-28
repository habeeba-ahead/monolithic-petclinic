#!/usr/bin/env python3
"""
Test script to verify Java 17 Maven configuration
"""
import xml.etree.ElementTree as ET
import subprocess
import sys
import os

def test_maven_java17_configuration():
    """Test that Maven is configured for Java 17"""
    results = {
        "tests_passed": True,
        "test_output": [],
        "criteria_met": [],
        "criteria_failed": []
    }
    
    # Test 1: Check Maven compiler source and target set to 17
    try:
        tree = ET.parse('pom.xml')
        root = tree.getroot()
        
        # Find properties section
        properties = root.find('.//{http://maven.apache.org/POM/4.0.0}properties')
        
        if properties is not None:
            # Check java.version
            java_version = properties.find('.//{http://maven.apache.org/POM/4.0.0}java.version')
            maven_compiler_source = properties.find('.//{http://maven.apache.org/POM/4.0.0}maven.compiler.source')
            maven_compiler_target = properties.find('.//{http://maven.apache.org/POM/4.0.0}maven.compiler.target')
            
            if java_version is not None and java_version.text == '17':
                results["criteria_met"].append("Java version property set to 17")
                results["test_output"].append("✓ java.version = 17")
            else:
                results["criteria_failed"].append("Java version property not set to 17")
                results["test_output"].append("✗ java.version not set to 17")
                results["tests_passed"] = False
                
            if maven_compiler_source is not None and maven_compiler_source.text == '17':
                results["criteria_met"].append("Maven compiler source set to 17")
                results["test_output"].append("✓ maven.compiler.source = 17")
            else:
                results["criteria_failed"].append("Maven compiler source not set to 17")
                results["test_output"].append("✗ maven.compiler.source not set to 17")
                results["tests_passed"] = False
                
            if maven_compiler_target is not None and maven_compiler_target.text == '17':
                results["criteria_met"].append("Maven compiler target set to 17")
                results["test_output"].append("✓ maven.compiler.target = 17")
            else:
                results["criteria_failed"].append("Maven compiler target not set to 17")
                results["test_output"].append("✗ maven.compiler.target not set to 17")
                results["tests_passed"] = False
        else:
            results["criteria_failed"].append("Properties section not found in pom.xml")
            results["test_output"].append("✗ Properties section not found")
            results["tests_passed"] = False
            
    except Exception as e:
        results["criteria_failed"].append(f"Error parsing pom.xml: {str(e)}")
        results["test_output"].append(f"✗ Error parsing pom.xml: {str(e)}")
        results["tests_passed"] = False
    
    # Test 2: Check Maven compiler plugin version
    try:
        tree = ET.parse('pom.xml')
        root = tree.getroot()
        
        # Find maven-compiler-plugin.version property
        properties = root.find('.//{http://maven.apache.org/POM/4.0.0}properties')
        if properties is not None:
            plugin_version = properties.find('.//{http://maven.apache.org/POM/4.0.0}maven-compiler-plugin.version')
            if plugin_version is not None:
                version = plugin_version.text
                # Check if version is 3.8.0 or higher (supports Java 17)
                version_parts = version.split('.')
                major = int(version_parts[0])
                minor = int(version_parts[1]) if len(version_parts) > 1 else 0
                
                if major > 3 or (major == 3 and minor >= 8):
                    results["criteria_met"].append("Maven compiler plugin version supports Java 17")
                    results["test_output"].append(f"✓ maven-compiler-plugin version = {version} (supports Java 17)")
                else:
                    results["criteria_failed"].append("Maven compiler plugin version too old for Java 17")
                    results["test_output"].append(f"✗ maven-compiler-plugin version = {version} (too old for Java 17)")
                    results["tests_passed"] = False
            else:
                results["criteria_failed"].append("Maven compiler plugin version not specified")
                results["test_output"].append("✗ maven-compiler-plugin version not specified")
                results["tests_passed"] = False
                
    except Exception as e:
        results["criteria_failed"].append(f"Error checking plugin version: {str(e)}")
        results["test_output"].append(f"✗ Error checking plugin version: {str(e)}")
        results["tests_passed"] = False
    
    # Test 3: Check that Maven compiler plugin is properly configured
    try:
        tree = ET.parse('pom.xml')
        root = tree.getroot()
        
        # Find the maven-compiler-plugin configuration
        plugin_mgmt = root.find('.//{http://maven.apache.org/POM/4.0.0}pluginManagement')
        if plugin_mgmt is not None:
            plugins = plugin_mgmt.find('.//{http://maven.apache.org/POM/4.0.0}plugins')
            if plugins is not None:
                for plugin in plugins.findall('.//{http://maven.apache.org/POM/4.0.0}plugin'):
                    artifact_id = plugin.find('.//{http://maven.apache.org/POM/4.0.0}artifactId')
                    if artifact_id is not None and artifact_id.text == 'maven-compiler-plugin':
                        config = plugin.find('.//{http://maven.apache.org/POM/4.0.0}configuration')
                        if config is not None:
                            source = config.find('.//{http://maven.apache.org/POM/4.0.0}source')
                            target = config.find('.//{http://maven.apache.org/POM/4.0.0}target')
                            
                            if source is not None and '${maven.compiler.source}' in source.text:
                                results["criteria_met"].append("Maven compiler plugin source configured correctly")
                                results["test_output"].append("✓ Maven compiler plugin source uses property reference")
                            else:
                                results["criteria_failed"].append("Maven compiler plugin source not configured correctly")
                                results["test_output"].append("✗ Maven compiler plugin source not configured correctly")
                                results["tests_passed"] = False
                                
                            if target is not None and '${maven.compiler.target}' in target.text:
                                results["criteria_met"].append("Maven compiler plugin target configured correctly")
                                results["test_output"].append("✓ Maven compiler plugin target uses property reference")
                            else:
                                results["criteria_failed"].append("Maven compiler plugin target not configured correctly")
                                results["test_output"].append("✗ Maven compiler plugin target not configured correctly")
                                results["tests_passed"] = False
                        break
                        
    except Exception as e:
        results["criteria_failed"].append(f"Error checking plugin configuration: {str(e)}")
        results["test_output"].append(f"✗ Error checking plugin configuration: {str(e)}")
        results["tests_passed"] = False
    
    return results

def test_compilation():
    """Test that project compiles successfully with Java 17"""
    results = {
        "compilation_passed": True,
        "compilation_output": [],
        "compilation_errors": []
    }
    
    try:
        # Check Java version first
        java_version_result = subprocess.run(['java', '-version'], 
                                           capture_output=True, text=True, timeout=30)
        results["compilation_output"].append(f"Java version check: {java_version_result.stderr}")
        
        # Try to compile the project
        compile_result = subprocess.run(['mvn', 'clean', 'compile', '-q'], 
                                      capture_output=True, text=True, timeout=300)
        
        if compile_result.returncode == 0:
            results["compilation_output"].append("✓ Project compiled successfully with Java 17")
        else:
            results["compilation_passed"] = False
            results["compilation_errors"].append("Project compilation failed")
            results["compilation_output"].append(f"✗ Compilation failed: {compile_result.stderr}")
            
    except subprocess.TimeoutExpired:
        results["compilation_passed"] = False
        results["compilation_errors"].append("Compilation timed out")
        results["compilation_output"].append("✗ Compilation timed out after 5 minutes")
    except Exception as e:
        results["compilation_passed"] = False
        results["compilation_errors"].append(f"Error during compilation: {str(e)}")
        results["compilation_output"].append(f"✗ Error during compilation: {str(e)}")
    
    return results

if __name__ == "__main__":
    print("Testing Java 17 Maven Configuration...")
    print("=" * 50)
    
    # Test Maven configuration
    config_results = test_maven_java17_configuration()
    
    print("Maven Configuration Tests:")
    for output in config_results["test_output"]:
        print(f"  {output}")
    
    print("\nCriteria Met:")
    for criterion in config_results["criteria_met"]:
        print(f"  ✓ {criterion}")
    
    if config_results["criteria_failed"]:
        print("\nCriteria Failed:")
        for criterion in config_results["criteria_failed"]:
            print(f"  ✗ {criterion}")
    
    # Test compilation
    print("\n" + "=" * 50)
    print("Testing Project Compilation...")
    
    compilation_results = test_compilation()
    
    print("Compilation Tests:")
    for output in compilation_results["compilation_output"]:
        print(f"  {output}")
    
    if compilation_results["compilation_errors"]:
        print("\nCompilation Errors:")
        for error in compilation_results["compilation_errors"]:
            print(f"  ✗ {error}")
    
    # Final results
    print("\n" + "=" * 50)
    print("FINAL RESULTS:")
    
    final_results = {
        "tests_passed": config_results["tests_passed"] and compilation_results["compilation_passed"],
        "test_output": "Maven configuration tests: " + "; ".join(config_results["test_output"]) + 
                      " | Compilation tests: " + "; ".join(compilation_results["compilation_output"]),
        "criteria_met": config_results["criteria_met"],
        "criteria_failed": config_results["criteria_failed"] + compilation_results["compilation_errors"]
    }
    
    if compilation_results["compilation_passed"]:
        final_results["criteria_met"].append("Project compiles successfully with Java 17")
    else:
        final_results["criteria_failed"].append("Project compilation failed")
    
    if not compilation_results["compilation_errors"]:
        final_results["criteria_met"].append("No Java version compatibility errors")
    else:
        final_results["criteria_failed"].append("Java version compatibility errors found")
    
    print(f"Overall Status: {'PASSED' if final_results['tests_passed'] else 'FAILED'}")
    print(f"Tests Passed: {final_results['tests_passed']}")
    print(f"Criteria Met: {len(final_results['criteria_met'])}")
    print(f"Criteria Failed: {len(final_results['criteria_failed'])}")
    
    # Output JSON for verification agent
    import json
    print("\nJSON Results:")
    print(json.dumps(final_results, indent=2))
    
    sys.exit(0 if final_results["tests_passed"] else 1)