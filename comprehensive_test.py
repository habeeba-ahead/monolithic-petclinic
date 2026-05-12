#!/usr/bin/env python3
"""
Comprehensive test script to verify all acceptance criteria for frontend dependency updates
"""
import re
import sys
import subprocess
import json
from packaging import version

def extract_property_value(pom_content, property_name):
    """Extract property value from pom.xml content"""
    pattern = f"<{property_name}>(.*?)</{property_name}>"
    match = re.search(pattern, pom_content)
    return match.group(1) if match else None

def is_version_at_least(actual_version, minimum_version):
    """Check if actual version meets minimum requirement"""
    try:
        return version.parse(actual_version) >= version.parse(minimum_version)
    except Exception as e:
        print(f"Error parsing versions: {e}")
        return False

def check_dependency_versions():
    """Check that all frontend dependencies meet minimum version requirements"""
    print("=== Checking Frontend Dependency Versions ===")
    
    try:
        with open('pom.xml', 'r') as f:
            pom_content = f.read()
    except FileNotFoundError:
        return False, ["pom.xml not found"]

    criteria_met = []
    criteria_failed = []
    
    # Test jQuery version >= 3.6.0
    jquery_version = extract_property_value(pom_content, 'webjars-jquery.version')
    if jquery_version and is_version_at_least(jquery_version, '3.6.0'):
        print(f"✅ jQuery updated to {jquery_version} (>= 3.6.0)")
        criteria_met.append(f"jQuery updated to {jquery_version} (>= 3.6.0)")
    else:
        print(f"❌ jQuery version requirement not met: {jquery_version}")
        criteria_failed.append(f"jQuery version requirement not met: {jquery_version}")

    # Test jQuery UI version >= 1.13.0
    jquery_ui_version = extract_property_value(pom_content, 'webjars-jquery-ui.version')
    if jquery_ui_version and is_version_at_least(jquery_ui_version, '1.13.0'):
        print(f"✅ jQuery UI updated to {jquery_ui_version} (>= 1.13.0)")
        criteria_met.append(f"jQuery UI updated to {jquery_ui_version} (>= 1.13.0)")
    else:
        print(f"❌ jQuery UI version requirement not met: {jquery_ui_version}")
        criteria_failed.append(f"jQuery UI version requirement not met: {jquery_ui_version}")

    # Test Bootstrap version >= 5.0.0
    bootstrap_version = extract_property_value(pom_content, 'webjars-bootstrap.version')
    if bootstrap_version and is_version_at_least(bootstrap_version, '5.0.0'):
        print(f"✅ Bootstrap updated to {bootstrap_version} (>= 5.0.0)")
        criteria_met.append(f"Bootstrap updated to {bootstrap_version} (>= 5.0.0)")
    else:
        print(f"❌ Bootstrap version requirement not met: {bootstrap_version}")
        criteria_failed.append(f"Bootstrap version requirement not met: {bootstrap_version}")

    return len(criteria_failed) == 0, criteria_met, criteria_failed

def check_pom_dependencies():
    """Check that dependencies are properly declared in pom.xml files"""
    print("\n=== Checking POM Dependency Declarations ===")
    
    criteria_met = []
    criteria_failed = []
    
    try:
        with open('monolith/pom.xml', 'r') as f:
            monolith_pom = f.read()
        
        required_deps = ['jquery', 'jquery-ui', 'bootstrap']
        for dep in required_deps:
            if f'<artifactId>{dep}</artifactId>' in monolith_pom and '${webjars-' in monolith_pom:
                print(f"✅ {dep} dependency properly declared with version property")
                criteria_met.append(f"{dep} dependency properly declared")
            else:
                print(f"❌ {dep} dependency not properly declared")
                criteria_failed.append(f"{dep} dependency not properly declared")
                
    except FileNotFoundError:
        print("❌ monolith/pom.xml not found")
        criteria_failed.append("monolith/pom.xml not found")

    return len(criteria_failed) == 0, criteria_met, criteria_failed

def check_template_integration():
    """Check that templates properly reference the updated dependencies"""
    print("\n=== Checking Template Integration ===")
    
    criteria_met = []
    criteria_failed = []
    
    try:
        with open('monolith/src/main/resources/templates/fragments/layout.html', 'r') as f:
            layout_content = f.read()
        
        # Check for proper webjar references
        webjar_refs = [
            '/webjars/jquery/jquery.min.js',
            '/webjars/jquery-ui/jquery-ui.min.js',
            '/webjars/bootstrap/js/bootstrap.min.js'
        ]
        
        for ref in webjar_refs:
            if ref in layout_content:
                print(f"✅ Template properly references {ref}")
                criteria_met.append(f"Template references {ref}")
            else:
                print(f"❌ Template missing reference to {ref}")
                criteria_failed.append(f"Template missing reference to {ref}")
                
    except FileNotFoundError:
        print("❌ layout.html template not found")
        criteria_failed.append("layout.html template not found")

    return len(criteria_failed) == 0, criteria_met, criteria_failed

def check_known_cves():
    """Check that the updated versions eliminate known CVEs"""
    print("\n=== Checking CVE Elimination ===")
    
    criteria_met = []
    criteria_failed = []
    
    try:
        with open('pom.xml', 'r') as f:
            pom_content = f.read()
    except FileNotFoundError:
        return False, [], ["pom.xml not found"]

    # Known CVEs and their fix versions
    cve_checks = [
        {
            'property': 'webjars-jquery.version',
            'min_version': '3.6.0',
            'cves': ['CVE-2019-11358', 'CVE-2020-11022', 'CVE-2020-11023']
        },
        {
            'property': 'webjars-bootstrap.version',
            'min_version': '5.0.0',
            'cves': ['CVE-2018-14040', 'CVE-2018-14041', 'CVE-2018-14042', 'CVE-2019-8331']
        }
    ]
    
    for check in cve_checks:
        actual_version = extract_property_value(pom_content, check['property'])
        if actual_version and is_version_at_least(actual_version, check['min_version']):
            cve_list = ', '.join(check['cves'])
            print(f"✅ {check['property']} {actual_version} eliminates: {cve_list}")
            criteria_met.append(f"Eliminated CVEs: {cve_list}")
        else:
            print(f"❌ {check['property']} {actual_version} may still have CVEs")
            criteria_failed.append(f"Potential CVEs in {check['property']} {actual_version}")

    return len(criteria_failed) == 0, criteria_met, criteria_failed

def run_comprehensive_test():
    """Run all tests and return comprehensive results"""
    print("Starting comprehensive frontend dependency security verification...\n")
    
    all_criteria_met = []
    all_criteria_failed = []
    all_tests_passed = True
    
    # Test 1: Dependency versions
    test1_passed, met1, failed1 = check_dependency_versions()
    all_criteria_met.extend(met1)
    all_criteria_failed.extend(failed1)
    all_tests_passed = all_tests_passed and test1_passed
    
    # Test 2: POM declarations
    test2_passed, met2, failed2 = check_pom_dependencies()
    all_criteria_met.extend(met2)
    all_criteria_failed.extend(failed2)
    all_tests_passed = all_tests_passed and test2_passed
    
    # Test 3: Template integration
    test3_passed, met3, failed3 = check_template_integration()
    all_criteria_met.extend(met3)
    all_criteria_failed.extend(failed3)
    all_tests_passed = all_tests_passed and test3_passed
    
    # Test 4: CVE elimination
    test4_passed, met4, failed4 = check_known_cves()
    all_criteria_met.extend(met4)
    all_criteria_failed.extend(failed4)
    all_tests_passed = all_tests_passed and test4_passed
    
    # Summary
    print(f"\n=== Test Summary ===")
    print(f"Tests passed: {all_tests_passed}")
    print(f"Criteria met: {len(all_criteria_met)}")
    print(f"Criteria failed: {len(all_criteria_failed)}")
    
    if all_tests_passed:
        print("🎉 All acceptance criteria have been met!")
    else:
        print("⚠️  Some acceptance criteria were not met.")
    
    return {
        "tests_passed": all_tests_passed,
        "test_output": f"Comprehensive frontend dependency security test completed. {len(all_criteria_met)} criteria met, {len(all_criteria_failed)} failed.",
        "criteria_met": all_criteria_met,
        "criteria_failed": all_criteria_failed
    }

if __name__ == "__main__":
    result = run_comprehensive_test()
    print(f"\n=== JSON Result ===")
    print(json.dumps(result, indent=2))
    sys.exit(0 if result["tests_passed"] else 1)