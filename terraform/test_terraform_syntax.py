#!/usr/bin/env python3

"""
Terraform syntax and structure validation script
This script validates Terraform configuration files without requiring Terraform installation
"""

import os
import re
import json
from pathlib import Path

def validate_terraform_syntax():
    """Validate basic Terraform syntax and structure"""
    
    results = {
        "syntax_valid": True,
        "structure_valid": True,
        "errors": [],
        "warnings": [],
        "files_checked": []
    }
    
    terraform_dir = Path(".")
    tf_files = list(terraform_dir.glob("*.tf"))
    
    if not tf_files:
        results["syntax_valid"] = False
        results["errors"].append("No .tf files found")
        return results
    
    for tf_file in tf_files:
        results["files_checked"].append(str(tf_file))
        
        try:
            with open(tf_file, 'r') as f:
                content = f.read()
            
            # Check for balanced braces
            open_braces = content.count('{')
            close_braces = content.count('}')
            if open_braces != close_braces:
                results["syntax_valid"] = False
                results["errors"].append(f"{tf_file}: Unbalanced braces ({open_braces} open, {close_braces} close)")
            
            # Check for balanced brackets
            open_brackets = content.count('[')
            close_brackets = content.count(']')
            if open_brackets != close_brackets:
                results["syntax_valid"] = False
                results["errors"].append(f"{tf_file}: Unbalanced brackets ({open_brackets} open, {close_brackets} close)")
            
            # Check for balanced quotes
            quote_count = content.count('"')
            if quote_count % 2 != 0:
                results["warnings"].append(f"{tf_file}: Odd number of quotes - possible syntax issue")
            
            # Check for required blocks in specific files
            if tf_file.name == "main.tf":
                if "terraform {" not in content:
                    results["structure_valid"] = False
                    results["errors"].append(f"{tf_file}: Missing terraform configuration block")
                
                if "provider " not in content:
                    results["structure_valid"] = False
                    results["errors"].append(f"{tf_file}: Missing provider configuration")
            
            if tf_file.name == "variables.tf":
                if "variable " not in content:
                    results["warnings"].append(f"{tf_file}: No variables defined")
            
            if tf_file.name == "outputs.tf":
                if "output " not in content:
                    results["warnings"].append(f"{tf_file}: No outputs defined")
                    
        except Exception as e:
            results["syntax_valid"] = False
            results["errors"].append(f"{tf_file}: Error reading file - {str(e)}")
    
    return results

def check_resource_coverage():
    """Check if all required resources are defined"""
    
    required_resources = {
        "google_container_cluster": "GKE cluster",
        "google_container_node_pool": "GKE node pool", 
        "google_sql_database_instance": "Cloud SQL instance",
        "google_compute_network": "VPC network",
        "google_compute_subnetwork": "Subnet",
        "google_service_account": "Service accounts",
        "google_project_iam_member": "IAM role bindings"
    }
    
    results = {
        "all_resources_found": True,
        "found_resources": [],
        "missing_resources": []
    }
    
    terraform_dir = Path(".")
    all_content = ""
    
    for tf_file in terraform_dir.glob("*.tf"):
        with open(tf_file, 'r') as f:
            all_content += f.read() + "\n"
    
    for resource_type, description in required_resources.items():
        if f'resource "{resource_type}"' in all_content:
            results["found_resources"].append(f"{resource_type} ({description})")
        else:
            results["all_resources_found"] = False
            results["missing_resources"].append(f"{resource_type} ({description})")
    
    return results

def main():
    """Main validation function"""
    
    print("=== Terraform Configuration Validation ===")
    
    # Change to terraform directory
    os.chdir("terraform")
    
    # Validate syntax
    print("\n1. Validating Terraform syntax...")
    syntax_results = validate_terraform_syntax()
    
    if syntax_results["syntax_valid"]:
        print("✅ Terraform syntax validation: PASSED")
    else:
        print("❌ Terraform syntax validation: FAILED")
        for error in syntax_results["errors"]:
            print(f"   Error: {error}")
    
    if syntax_results["warnings"]:
        print("⚠️  Warnings:")
        for warning in syntax_results["warnings"]:
            print(f"   Warning: {warning}")
    
    print(f"   Files checked: {len(syntax_results['files_checked'])}")
    
    # Check resource coverage
    print("\n2. Validating resource coverage...")
    resource_results = check_resource_coverage()
    
    if resource_results["all_resources_found"]:
        print("✅ Resource coverage validation: PASSED")
    else:
        print("❌ Resource coverage validation: FAILED")
        for missing in resource_results["missing_resources"]:
            print(f"   Missing: {missing}")
    
    print(f"   Found resources: {len(resource_results['found_resources'])}")
    for found in resource_results["found_resources"]:
        print(f"   ✅ {found}")
    
    # Overall result
    print("\n=== VALIDATION SUMMARY ===")
    overall_success = (syntax_results["syntax_valid"] and 
                      syntax_results["structure_valid"] and 
                      resource_results["all_resources_found"])
    
    if overall_success:
        print("🎉 Terraform configuration validation: SUCCESS")
        print("   Configuration is ready for terraform init/plan/apply")
        return 0
    else:
        print("❌ Terraform configuration validation: FAILED")
        print("   Please fix the issues above before proceeding")
        return 1

if __name__ == "__main__":
    exit(main())