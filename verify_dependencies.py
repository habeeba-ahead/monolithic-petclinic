#!/usr/bin/env python3
"""
Script to verify that frontend dependencies have been upgraded to eliminate XSS vulnerabilities
"""

import re
import sys
from packaging import version

def read_file(filepath):
    """Read file content"""
    try:
        with open(filepath, 'r') as f:
            return f.read()
    except FileNotFoundError:
        print(f"Error: File {filepath} not found")
        return None

def extract_property_value(content, property_name):
    """Extract property value from pom.xml content"""
    pattern = f'<{property_name}>(.*?)</{property_name}>'
    match = re.search(pattern, content)
    if match:
        return match.group(1)
    return None

def verify_version(actual_version, minimum_version, dependency_name):
    """Verify that actual version meets minimum requirement"""
    try:
        if version.parse(actual_version) >= version.parse(minimum_version):
            print(f"✓ {dependency_name}: {actual_version} (meets requirement: {minimum_version}+)")
            return True
        else:
            print(f"✗ {dependency_name}: {actual_version} (does not meet requirement: {minimum_version}+)")
            return False
    except Exception as e:
        print(f"✗ {dependency_name}: Error parsing version {actual_version}: {e}")
        return False

def main():
    """Main verification function"""
    print("Verifying frontend dependency upgrades...")
    print("=" * 50)
    
    # Read pom.xml content
    pom_content = read_file('pom.xml')
    if not pom_content:
        sys.exit(1)
    
    # Extract versions
    jquery_version = extract_property_value(pom_content, 'webjars-jquery.version')
    jquery_ui_version = extract_property_value(pom_content, 'webjars-jquery-ui.version')
    bootstrap_version = extract_property_value(pom_content, 'webjars-bootstrap.version')
    
    if not all([jquery_version, jquery_ui_version, bootstrap_version]):
        print("Error: Could not extract all dependency versions from pom.xml")
        sys.exit(1)
    
    # Verify versions meet requirements
    results = []
    results.append(verify_version(jquery_version, "3.6.0", "jQuery"))
    results.append(verify_version(jquery_ui_version, "1.13.0", "jQuery UI"))
    results.append(verify_version(bootstrap_version, "5.0.0", "Bootstrap"))
    
    print("\n" + "=" * 50)
    if all(results):
        print("✓ All dependency versions meet security requirements!")
        return True
    else:
        print("✗ Some dependency versions do not meet security requirements!")
        return False

if __name__ == "__main__":
    success = main()
    sys.exit(0 if success else 1)