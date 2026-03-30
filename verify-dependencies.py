#!/usr/bin/env python3
"""
Test script to verify Spring Boot starter dependencies acceptance criteria:
1. All Spring Boot starter dependencies use parent version (no explicit versions)
2. No version conflicts in dependency resolution
3. Maven clean compile succeeds
"""

import subprocess
import sys
import xml.etree.ElementTree as ET
import re

def run_command(cmd, cwd="."):
    """Run a shell command and return the result"""
    try:
        result = subprocess.run(cmd, shell=True, cwd=cwd, capture_output=True, text=True)
        return result.returncode, result.stdout, result.stderr
    except Exception as e:
        return 1, "", str(e)

def check_spring_boot_starter_versions():
    """Check that Spring Boot starter dependencies don't have explicit versions"""
    print("=== Checking Spring Boot starter dependency versions ===")
    
    pom_files = ["pom.xml", "monolith/pom.xml", "activemq/pom.xml"]
    spring_boot_starters = []
    version_violations = []
    
    for pom_file in pom_files:
        try:
            tree = ET.parse(pom_file)
            root = tree.getroot()
            
            # Define namespace
            ns = {'maven': 'http://maven.apache.org/POM/4.0.0'}
            
            # Find all dependencies
            dependencies = root.findall('.//maven:dependency', ns)
            
            for dep in dependencies:
                group_id = dep.find('maven:groupId', ns)
                artifact_id = dep.find('maven:artifactId', ns)
                version = dep.find('maven:version', ns)
                
                if group_id is not None and artifact_id is not None:
                    group_text = group_id.text
                    artifact_text = artifact_id.text
                    
                    # Check if it's a Spring Boot starter
                    if (group_text == 'org.springframework.boot' and 
                        artifact_text.startswith('spring-boot-starter')):
                        
                        spring_boot_starters.append(f"{group_text}:{artifact_text}")
                        
                        # Check if it has an explicit version
                        if version is not None:
                            version_violations.append(f"{pom_file}: {group_text}:{artifact_text} has explicit version {version.text}")
                            
        except ET.ParseError as e:
            print(f"Error parsing {pom_file}: {e}")
            return False
        except FileNotFoundError:
            print(f"File not found: {pom_file}")
            continue
    
    print(f"Found {len(spring_boot_starters)} Spring Boot starter dependencies:")
    for starter in spring_boot_starters:
        print(f"  - {starter}")
    
    if version_violations:
        print("\nVERSION VIOLATIONS FOUND:")
        for violation in version_violations:
            print(f"  ❌ {violation}")
        return False
    else:
        print("\n✅ All Spring Boot starter dependencies use parent version (no explicit versions)")
        return True

def check_dependency_conflicts():
    """Check for dependency version conflicts using Maven"""
    print("\n=== Checking for dependency version conflicts ===")
    
    # Run Maven dependency:tree to check for conflicts
    returncode, stdout, stderr = run_command("mvn dependency:tree -Dverbose")
    
    if returncode != 0:
        print(f"❌ Maven dependency:tree failed:")
        print(f"STDOUT: {stdout}")
        print(f"STDERR: {stderr}")
        return False
    
    # Look for conflict indicators in the output
    conflict_patterns = [
        r"conflicts with",
        r"omitted for conflict",
        r"version conflict",
        r"Dependency convergence error"
    ]
    
    conflicts_found = []
    for pattern in conflict_patterns:
        matches = re.findall(pattern, stdout, re.IGNORECASE)
        if matches:
            conflicts_found.extend(matches)
    
    if conflicts_found:
        print(f"❌ Dependency conflicts detected:")
        for conflict in conflicts_found:
            print(f"  - {conflict}")
        return False
    else:
        print("✅ No dependency version conflicts detected")
        return True

def check_maven_compile():
    """Check that Maven clean compile succeeds"""
    print("\n=== Testing Maven clean compile ===")
    
    # First run clean
    print("Running mvn clean...")
    returncode, stdout, stderr = run_command("mvn clean")
    
    if returncode != 0:
        print(f"❌ Maven clean failed:")
        print(f"STDOUT: {stdout}")
        print(f"STDERR: {stderr}")
        return False
    
    # Then run compile
    print("Running mvn compile...")
    returncode, stdout, stderr = run_command("mvn compile")
    
    if returncode != 0:
        print(f"❌ Maven compile failed:")
        print(f"STDOUT: {stdout}")
        print(f"STDERR: {stderr}")
        return False
    else:
        print("✅ Maven clean compile succeeded")
        return True

def main():
    """Main test function"""
    print("Spring Boot Dependencies Verification Test")
    print("=" * 50)
    
    criteria_results = {
        "spring_boot_versions": check_spring_boot_starter_versions(),
        "no_conflicts": check_dependency_conflicts(),
        "maven_compile": check_maven_compile()
    }
    
    print("\n" + "=" * 50)
    print("VERIFICATION RESULTS:")
    
    all_passed = True
    criteria_met = []
    criteria_failed = []
    
    if criteria_results["spring_boot_versions"]:
        print("✅ All Spring Boot starter dependencies use parent version")
        criteria_met.append("All Spring Boot starter dependencies use parent version")
    else:
        print("❌ Some Spring Boot starter dependencies have explicit versions")
        criteria_failed.append("All Spring Boot starter dependencies use parent version")
        all_passed = False
    
    if criteria_results["no_conflicts"]:
        print("✅ No version conflicts in dependency resolution")
        criteria_met.append("No version conflicts in dependency resolution")
    else:
        print("❌ Version conflicts detected in dependency resolution")
        criteria_failed.append("No version conflicts in dependency resolution")
        all_passed = False
    
    if criteria_results["maven_compile"]:
        print("✅ Maven clean compile succeeds")
        criteria_met.append("Maven clean compile succeeds")
    else:
        print("❌ Maven clean compile failed")
        criteria_failed.append("Maven clean compile succeeds")
        all_passed = False
    
    # Output JSON summary
    import json
    summary = {
        "tests_passed": all_passed,
        "test_output": f"Spring Boot dependencies verification completed. {len(criteria_met)} criteria met, {len(criteria_failed)} criteria failed.",
        "criteria_met": criteria_met,
        "criteria_failed": criteria_failed
    }
    
    print("\nJSON SUMMARY:")
    print(json.dumps(summary, indent=2))
    
    return 0 if all_passed else 1

if __name__ == "__main__":
    sys.exit(main())