#!/usr/bin/env python3
"""
Verification script to check if Spring Security implementation meets acceptance criteria.
"""

import os
import re
import xml.etree.ElementTree as ET

def check_spring_security_dependency():
    """Check if spring-boot-starter-security dependency is added to pom.xml"""
    pom_path = "monolith/pom.xml"
    
    if not os.path.exists(pom_path):
        return False, "pom.xml not found"
    
    try:
        tree = ET.parse(pom_path)
        root = tree.getroot()
        
        # Define namespace
        ns = {'maven': 'http://maven.apache.org/POM/4.0.0'}
        
        # Look for spring-boot-starter-security dependency
        dependencies = root.findall('.//maven:dependency', ns)
        for dep in dependencies:
            artifact_id = dep.find('maven:artifactId', ns)
            if artifact_id is not None and artifact_id.text == 'spring-boot-starter-security':
                return True, "spring-boot-starter-security dependency found"
        
        # Fallback: check as text if XML parsing doesn't work due to namespace issues
        with open(pom_path, 'r') as f:
            content = f.read()
            if 'spring-boot-starter-security' in content:
                return True, "spring-boot-starter-security dependency found in pom.xml"
        
        return False, "spring-boot-starter-security dependency not found"
    except Exception as e:
        return False, f"Error checking pom.xml: {str(e)}"

def check_security_config_class():
    """Check if SecurityConfig class exists with basic configuration"""
    config_path = "monolith/src/main/java/org/springframework/samples/petclinic/config/SecurityConfig.java"
    
    if not os.path.exists(config_path):
        return False, "SecurityConfig.java not found"
    
    try:
        with open(config_path, 'r') as f:
            content = f.read()
        
        # Check for required annotations and methods
        required_elements = [
            '@Configuration',
            '@EnableWebSecurity',
            'SecurityFilterChain',
            'securityFilterChain',
            'HttpSecurity'
        ]
        
        missing_elements = []
        for element in required_elements:
            if element not in content:
                missing_elements.append(element)
        
        if missing_elements:
            return False, f"Missing elements in SecurityConfig: {', '.join(missing_elements)}"
        
        return True, "SecurityConfig class has all required elements"
    except Exception as e:
        return False, f"Error checking SecurityConfig.java: {str(e)}"

def check_application_class():
    """Check if Application class exists (for startup verification)"""
    app_path = "monolith/src/main/java/org/springframework/samples/petclinic/Application.java"
    
    if not os.path.exists(app_path):
        return False, "Application.java not found"
    
    try:
        with open(app_path, 'r') as f:
            content = f.read()
        
        # Check for Spring Boot application annotation
        if '@SpringBootApplication' in content and 'SpringApplication.run' in content:
            return True, "Application class properly configured"
        else:
            return False, "Application class missing required Spring Boot configuration"
    except Exception as e:
        return False, f"Error checking Application.java: {str(e)}"

def main():
    """Main verification function"""
    print("Verifying Spring Security implementation...")
    print("=" * 50)
    
    criteria_results = {}
    
    # Check each acceptance criterion
    print("1. Checking spring-boot-starter-security dependency...")
    success, message = check_spring_security_dependency()
    criteria_results['dependency'] = success
    print(f"   {'✓' if success else '✗'} {message}")
    
    print("\n2. Checking SecurityConfig class...")
    success, message = check_security_config_class()
    criteria_results['security_config'] = success
    print(f"   {'✓' if success else '✗'} {message}")
    
    print("\n3. Checking Application class (for startup capability)...")
    success, message = check_application_class()
    criteria_results['application_class'] = success
    print(f"   {'✓' if success else '✗'} {message}")
    
    # Summary
    print("\n" + "=" * 50)
    print("VERIFICATION SUMMARY:")
    
    all_passed = all(criteria_results.values())
    passed_criteria = [k for k, v in criteria_results.items() if v]
    failed_criteria = [k for k, v in criteria_results.items() if not v]
    
    print(f"Overall Status: {'PASS' if all_passed else 'FAIL'}")
    print(f"Passed: {len(passed_criteria)}/{len(criteria_results)}")
    
    if passed_criteria:
        print(f"✓ Passed criteria: {', '.join(passed_criteria)}")
    if failed_criteria:
        print(f"✗ Failed criteria: {', '.join(failed_criteria)}")
    
    return {
        'tests_passed': all_passed,
        'criteria_met': passed_criteria,
        'criteria_failed': failed_criteria
    }

if __name__ == "__main__":
    main()