#!/usr/bin/env python3
"""
Final verification test for Spring Boot 2.2.1 dependency analysis.
"""

import csv
import json
from pathlib import Path


def verify_acceptance_criteria():
    """Verify all acceptance criteria are met."""
    
    results = {
        "tests_passed": True,
        "test_output": "",
        "criteria_met": [],
        "criteria_failed": []
    }
    
    output_lines = []
    
    # Check files exist
    files_to_check = [
        "spring-boot-dependency-analysis.md",
        "spring-boot-3-compatibility-matrix.csv", 
        "spring-boot-3-migration-checklist.md",
        "pom.xml",
        "monolith/pom.xml",
        "activemq/pom.xml"
    ]
    
    for file_path in files_to_check:
        if not Path(file_path).exists():
            results["criteria_failed"].append(f"Missing file: {file_path}")
            results["tests_passed"] = False
            output_lines.append(f"❌ Missing file: {file_path}")
        else:
            output_lines.append(f"✅ Found file: {file_path}")
    
    # Criterion 1: Complete inventory of all Spring Boot dependencies documented
    try:
        analysis_content = Path("spring-boot-dependency-analysis.md").read_text()
        
        # Check for key sections and dependencies
        required_inventory_items = [
            "Spring Boot Version**: 2.2.1.RELEASE",
            "Java Version**: 1.8", 
            "spring-boot-starter-data-jpa",
            "spring-boot-starter-web",
            "spring-boot-starter-thymeleaf",
            "spring-boot-starter-activemq",
            "Bootstrap**: 3.3.6",
            "jQuery**: 2.2.4",
            "jQuery UI**: 1.11.4",
            "WRO4J**: 1.8.0",
            "activemq-broker",
            "activemq-pool",
            "hawtio-springboot:2.8.0",
            "wiremock-jre8:2.26.0",
            "hsqldb"
        ]
        
        missing_items = []
        for item in required_inventory_items:
            if item not in analysis_content:
                missing_items.append(item)
        
        if missing_items:
            results["criteria_failed"].append("Complete dependency inventory - missing items: " + str(missing_items))
            results["tests_passed"] = False
            output_lines.append(f"❌ Missing inventory items: {missing_items}")
        else:
            results["criteria_met"].append("Complete inventory of all Spring Boot dependencies documented")
            output_lines.append("✅ Complete dependency inventory documented")
            
    except Exception as e:
        results["criteria_failed"].append(f"Error reading dependency analysis: {e}")
        results["tests_passed"] = False
        output_lines.append(f"❌ Error reading dependency analysis: {e}")
    
    # Criterion 2: Compatibility matrix created for Spring Boot 3.2.x migration
    try:
        # Check CSV matrix exists and has required structure
        with open("spring-boot-3-compatibility-matrix.csv", 'r') as f:
            csv_reader = csv.DictReader(f)
            headers = csv_reader.fieldnames
            
            required_headers = [
                'Dependency', 'Current Version', 'Spring Boot 3.2.x Compatible Version',
                'Compatibility Status', 'Migration Effort', 'Breaking Changes'
            ]
            
            missing_headers = []
            for header in required_headers:
                if header not in headers:
                    missing_headers.append(header)
            
            if missing_headers:
                results["criteria_failed"].append("Compatibility matrix missing headers: " + str(missing_headers))
                results["tests_passed"] = False
                output_lines.append(f"❌ Missing CSV headers: {missing_headers}")
            else:
                # Check for key dependencies in matrix
                rows = list(csv_reader)
                dependency_names = [row['Dependency'] for row in rows]
                
                key_dependencies = [
                    'Spring Boot Parent', 'Java Version', 'jQuery', 'Bootstrap',
                    'ActiveMQ Broker', 'WRO4J'
                ]
                
                missing_deps = []
                for dep in key_dependencies:
                    if dep not in dependency_names:
                        missing_deps.append(dep)
                
                if missing_deps:
                    results["criteria_failed"].append("Compatibility matrix missing dependencies: " + str(missing_deps))
                    results["tests_passed"] = False
                    output_lines.append(f"❌ Missing dependencies in matrix: {missing_deps}")
                else:
                    results["criteria_met"].append("Compatibility matrix created for Spring Boot 3.2.x migration")
                    output_lines.append(f"✅ Compatibility matrix created with {len(rows)} dependencies")
                    
    except Exception as e:
        results["criteria_failed"].append(f"Error reading compatibility matrix: {e}")
        results["tests_passed"] = False
        output_lines.append(f"❌ Error reading compatibility matrix: {e}")
    
    # Criterion 3: Breaking changes identified and documented
    try:
        analysis_content = Path("spring-boot-dependency-analysis.md").read_text()
        checklist_content = Path("spring-boot-3-migration-checklist.md").read_text()
        
        # Check for breaking changes documentation
        breaking_change_indicators = [
            "Critical Breaking Changes",
            "Java Version Requirement",
            "Javax to Jakarta Migration",
            "javax.* packages → jakarta.*",
            "Java 17+",
            "Spring Framework**: 6.x",
            "High-Risk Dependencies",
            "WRO4J (1.8.0)",
            "Risk Level**: HIGH"
        ]
        
        missing_breaking_changes = []
        for indicator in breaking_change_indicators:
            if indicator not in analysis_content:
                missing_breaking_changes.append(indicator)
        
        # Check migration checklist has breaking changes
        checklist_indicators = [
            "Namespace Migration (javax → jakarta)",
            "javax.persistence.*",
            "jakarta.persistence.*"
        ]
        
        for indicator in checklist_indicators:
            if indicator not in checklist_content:
                missing_breaking_changes.append(f"Checklist: {indicator}")
        
        if missing_breaking_changes:
            results["criteria_failed"].append("Breaking changes documentation incomplete: " + str(missing_breaking_changes))
            results["tests_passed"] = False
            output_lines.append(f"❌ Missing breaking changes documentation: {missing_breaking_changes}")
        else:
            results["criteria_met"].append("Breaking changes identified and documented")
            output_lines.append("✅ Breaking changes identified and documented")
            
    except Exception as e:
        results["criteria_failed"].append(f"Error reading breaking changes documentation: {e}")
        results["tests_passed"] = False
        output_lines.append(f"❌ Error reading breaking changes documentation: {e}")
    
    # Additional verification: Migration strategy exists
    try:
        analysis_content = Path("spring-boot-dependency-analysis.md").read_text()
        checklist_content = Path("spring-boot-3-migration-checklist.md").read_text()
        
        if "Migration Strategy" in analysis_content and "Migration Phase" in checklist_content:
            results["criteria_met"].append("Migration strategy and checklist provided")
            output_lines.append("✅ Migration strategy and checklist provided")
        else:
            output_lines.append("⚠️  Migration strategy documentation could be improved")
            
    except Exception as e:
        output_lines.append(f"⚠️  Could not verify migration strategy: {e}")
    
    # Compile test output
    results["test_output"] = "\n".join(output_lines)
    
    return results


if __name__ == '__main__':
    print("🔍 Final Verification of Spring Boot 2.2.1 Dependency Analysis")
    print("=" * 70)
    
    results = verify_acceptance_criteria()
    
    print(results["test_output"])
    print("\n" + "=" * 70)
    print("📊 FINAL RESULTS")
    print("=" * 70)
    
    if results["tests_passed"]:
        print("🎉 ALL ACCEPTANCE CRITERIA MET!")
    else:
        print("❌ Some acceptance criteria not fully met")
    
    print(f"\n✅ Criteria Met ({len(results['criteria_met'])}):")
    for criterion in results["criteria_met"]:
        print(f"  • {criterion}")
    
    if results["criteria_failed"]:
        print(f"\n❌ Criteria Failed ({len(results['criteria_failed'])}):")
        for criterion in results["criteria_failed"]:
            print(f"  • {criterion}")
    
    # Output JSON result
    print(f"\n📋 JSON Summary:")
    print(json.dumps(results, indent=2))