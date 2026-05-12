#!/usr/bin/env python3
"""
Script to verify that frontend dependencies meet security requirements
"""
import re
import sys
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

def main():
    # Read the root pom.xml
    try:
        with open('pom.xml', 'r') as f:
            pom_content = f.read()
    except FileNotFoundError:
        print("ERROR: pom.xml not found")
        return False

    # Test criteria
    tests = [
        {
            'name': 'jQuery version >= 3.6.0',
            'property': 'webjars-jquery.version',
            'minimum': '3.6.0',
            'cve_info': 'CVE-2019-11358'
        },
        {
            'name': 'jQuery UI version >= 1.13.0',
            'property': 'webjars-jquery-ui.version',
            'minimum': '1.13.0',
            'cve_info': 'Multiple CVEs in older versions'
        },
        {
            'name': 'Bootstrap version >= 5.0.0',
            'property': 'webjars-bootstrap.version',
            'minimum': '5.0.0',
            'cve_info': 'CVE-2018-14040, CVE-2018-14041, CVE-2018-14042'
        }
    ]

    all_passed = True
    results = []

    for test in tests:
        actual_version = extract_property_value(pom_content, test['property'])
        
        if not actual_version:
            print(f"❌ FAIL: {test['name']} - Property {test['property']} not found")
            all_passed = False
            results.append(f"Property {test['property']} not found")
            continue
            
        if is_version_at_least(actual_version, test['minimum']):
            print(f"✅ PASS: {test['name']} - Found version {actual_version} (eliminates {test['cve_info']})")
            results.append(f"{test['name']} - {actual_version}")
        else:
            print(f"❌ FAIL: {test['name']} - Found version {actual_version}, required >= {test['minimum']} (to eliminate {test['cve_info']})")
            all_passed = False
            results.append(f"{test['name']} - {actual_version} < {test['minimum']}")

    # Check that dependencies are properly declared in monolith module
    try:
        with open('monolith/pom.xml', 'r') as f:
            monolith_pom = f.read()
        
        required_deps = ['jquery', 'jquery-ui', 'bootstrap']
        for dep in required_deps:
            if f'<artifactId>{dep}</artifactId>' in monolith_pom:
                print(f"✅ PASS: {dep} dependency properly declared in monolith module")
            else:
                print(f"❌ FAIL: {dep} dependency not found in monolith module")
                all_passed = False
                
    except FileNotFoundError:
        print("❌ FAIL: monolith/pom.xml not found")
        all_passed = False

    return all_passed, results

if __name__ == "__main__":
    success, results = main()
    sys.exit(0 if success else 1)