#!/usr/bin/env python3
"""
Verify that build configuration is compatible with updated dependencies
"""
import re

def check_wro4j_config():
    """Check that wro4j configuration references correct Bootstrap version"""
    print("=== Checking wro4j Configuration ===")
    
    try:
        with open('monolith/src/main/wro/wro.xml', 'r') as f:
            wro_content = f.read()
        
        # Check for Bootstrap 5.x reference
        if 'bootstrap/5.' in wro_content:
            bootstrap_match = re.search(r'bootstrap/(\d+\.\d+\.\d+)', wro_content)
            if bootstrap_match:
                version = bootstrap_match.group(1)
                print(f"✅ wro.xml references Bootstrap {version}")
                return True, f"wro.xml references Bootstrap {version}"
            else:
                print("❌ Could not extract Bootstrap version from wro.xml")
                return False, "Could not extract Bootstrap version from wro.xml"
        else:
            print("❌ wro.xml does not reference Bootstrap 5.x")
            return False, "wro.xml does not reference Bootstrap 5.x"
            
    except FileNotFoundError:
        print("❌ wro.xml not found")
        return False, "wro.xml not found"

def check_maven_plugin_config():
    """Check that Maven wro4j plugin references correct Bootstrap version"""
    print("\n=== Checking Maven Plugin Configuration ===")
    
    try:
        with open('monolith/pom.xml', 'r') as f:
            pom_content = f.read()
        
        # Check that wro4j plugin has Bootstrap dependency with version property
        if 'wro4j-maven-plugin' in pom_content and '${webjars-bootstrap.version}' in pom_content:
            print("✅ Maven wro4j plugin properly configured with Bootstrap version property")
            return True, "Maven wro4j plugin properly configured"
        else:
            print("❌ Maven wro4j plugin not properly configured")
            return False, "Maven wro4j plugin not properly configured"
            
    except FileNotFoundError:
        print("❌ monolith/pom.xml not found")
        return False, "monolith/pom.xml not found"

def main():
    """Run all build configuration checks"""
    print("Verifying build configuration compatibility...\n")
    
    criteria_met = []
    criteria_failed = []
    
    # Check wro4j config
    wro_passed, wro_result = check_wro4j_config()
    if wro_passed:
        criteria_met.append(wro_result)
    else:
        criteria_failed.append(wro_result)
    
    # Check Maven plugin config
    maven_passed, maven_result = check_maven_plugin_config()
    if maven_passed:
        criteria_met.append(maven_result)
    else:
        criteria_failed.append(maven_result)
    
    all_passed = len(criteria_failed) == 0
    
    print(f"\n=== Build Configuration Summary ===")
    print(f"All checks passed: {all_passed}")
    print(f"Criteria met: {len(criteria_met)}")
    print(f"Criteria failed: {len(criteria_failed)}")
    
    return all_passed, criteria_met, criteria_failed

if __name__ == "__main__":
    success, met, failed = main()
    if success:
        print("🎉 Build configuration is compatible with updated dependencies!")
    else:
        print("⚠️  Build configuration issues detected.")