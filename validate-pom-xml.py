#!/usr/bin/env python3
"""
Additional validation for POM XML structure
"""

import xml.etree.ElementTree as ET
import sys

def validate_pom_xml():
    """Validate that the POM XML is well-formed and has required structure"""
    try:
        tree = ET.parse('pom.xml')
        root = tree.getroot()
        
        # Check root element
        if not root.tag.endswith('project'):
            return False, f"Root element is not 'project': {root.tag}"
        
        # Check required elements exist
        required_elements = ['modelVersion', 'parent', 'groupId', 'artifactId', 'version']
        missing_elements = []
        
        for element in required_elements:
            if root.find(f'.//{{{root.nsmap[None] if hasattr(root, "nsmap") and None in root.nsmap else "http://maven.apache.org/POM/4.0.0"}}}{element}') is None:
                # Try without namespace
                if root.find(f'.//{element}') is None:
                    missing_elements.append(element)
        
        if missing_elements:
            return False, f"Missing required elements: {', '.join(missing_elements)}"
        
        return True, "POM XML is well-formed and contains required elements"
        
    except ET.ParseError as e:
        return False, f"XML parsing error: {str(e)}"
    except Exception as e:
        return False, f"Validation error: {str(e)}"

def main():
    passed, message = validate_pom_xml()
    status = "PASS" if passed else "FAIL"
    print(f"[{status}] POM XML Validation")
    print(f"       {message}")
    return 0 if passed else 1

if __name__ == "__main__":
    sys.exit(main())