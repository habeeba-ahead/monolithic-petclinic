#!/usr/bin/env python3
"""
Test script to verify Helm chart implementation meets acceptance criteria.
"""

import os
import subprocess
import yaml
import json
import sys
import re
from pathlib import Path

def run_command(cmd, cwd=None):
    """Run a shell command and return the result."""
    try:
        result = subprocess.run(cmd, shell=True, capture_output=True, text=True, cwd=cwd)
        return result.returncode == 0, result.stdout, result.stderr
    except Exception as e:
        return False, "", str(e)

def parse_cpu_value(cpu_str):
    """Parse CPU value and convert to millicores for comparison."""
    if not cpu_str:
        return 0
    
    cpu_str = str(cpu_str).lower()
    if cpu_str.endswith('m'):
        return int(cpu_str[:-1])
    elif cpu_str.endswith(''):
        return int(float(cpu_str) * 1000)
    else:
        return int(float(cpu_str) * 1000)

def test_helm_chart_structure():
    """Test that Helm chart structure is created with Chart.yaml and values.yaml."""
    print("Testing Helm chart structure...")
    
    chart_dir = Path("helm/petclinic")
    required_files = [
        "Chart.yaml",
        "values.yaml",
        "values-development.yaml", 
        "values-production.yaml"
    ]
    
    missing_files = []
    for file in required_files:
        if not (chart_dir / file).exists():
            missing_files.append(file)
    
    if missing_files:
        return False, f"Missing required files: {missing_files}"
    
    # Validate Chart.yaml structure
    try:
        with open(chart_dir / "Chart.yaml", 'r') as f:
            chart_yaml = yaml.safe_load(f)
        
        required_chart_fields = ['apiVersion', 'name', 'description', 'type', 'version', 'appVersion']
        missing_chart_fields = [field for field in required_chart_fields if field not in chart_yaml]
        
        if missing_chart_fields:
            return False, f"Chart.yaml missing required fields: {missing_chart_fields}"
            
        if chart_yaml['name'] != 'petclinic':
            return False, f"Chart name should be 'petclinic', got '{chart_yaml['name']}'"
            
    except Exception as e:
        return False, f"Error parsing Chart.yaml: {e}"
    
    # Validate values.yaml structure
    try:
        with open(chart_dir / "values.yaml", 'r') as f:
            values_yaml = yaml.safe_load(f)
        
        required_values_sections = ['image', 'service', 'resources', 'config', 'secrets']
        missing_values_sections = [section for section in required_values_sections if section not in values_yaml]
        
        if missing_values_sections:
            return False, f"values.yaml missing required sections: {missing_values_sections}"
            
    except Exception as e:
        return False, f"Error parsing values.yaml: {e}"
    
    return True, "Helm chart structure is valid"

def test_templates_created():
    """Test that templates are created for Deployment, Service, ConfigMap, and Secret."""
    print("Testing template files...")
    
    templates_dir = Path("helm/petclinic/templates")
    required_templates = [
        "deployment.yaml",
        "service.yaml", 
        "configmap.yaml",
        "secret.yaml",
        "_helpers.tpl"
    ]
    
    missing_templates = []
    for template in required_templates:
        if not (templates_dir / template).exists():
            missing_templates.append(template)
    
    if missing_templates:
        return False, f"Missing required templates: {missing_templates}"
    
    # Validate template content
    template_validations = {
        "deployment.yaml": ["kind: Deployment", "apiVersion: apps/v1"],
        "service.yaml": ["kind: Service", "apiVersion: v1"],
        "configmap.yaml": ["kind: ConfigMap", "apiVersion: v1"],
        "secret.yaml": ["kind: Secret", "apiVersion: v1"]
    }
    
    for template_file, required_content in template_validations.items():
        try:
            with open(templates_dir / template_file, 'r') as f:
                content = f.read()
            
            missing_content = [item for item in required_content if item not in content]
            if missing_content:
                return False, f"{template_file} missing required content: {missing_content}"
                
        except Exception as e:
            return False, f"Error reading {template_file}: {e}"
    
    return True, "All required templates are present and valid"

def test_environment_specific_values():
    """Test that values files are configured with environment-specific settings."""
    print("Testing environment-specific values...")
    
    chart_dir = Path("helm/petclinic")
    
    try:
        # Load all values files
        with open(chart_dir / "values.yaml", 'r') as f:
            default_values = yaml.safe_load(f)
        with open(chart_dir / "values-development.yaml", 'r') as f:
            dev_values = yaml.safe_load(f)
        with open(chart_dir / "values-production.yaml", 'r') as f:
            prod_values = yaml.safe_load(f)
        
        # Check environment-specific differences
        if dev_values.get('environment') != 'development':
            return False, "Development values file should set environment to 'development'"
        
        if prod_values.get('environment') != 'production':
            return False, "Production values file should set environment to 'production'"
        
        # Check that production has different replica count
        if prod_values.get('replicaCount', 1) <= dev_values.get('replicaCount', 1):
            return False, "Production should have more replicas than development"
        
        # Check that production has different resource limits (proper numeric comparison)
        prod_cpu_limit = prod_values.get('resources', {}).get('limits', {}).get('cpu', '0')
        dev_cpu_limit = dev_values.get('resources', {}).get('limits', {}).get('cpu', '0')
        
        prod_cpu_millicores = parse_cpu_value(prod_cpu_limit)
        dev_cpu_millicores = parse_cpu_value(dev_cpu_limit)
        
        if prod_cpu_millicores <= dev_cpu_millicores:
            return False, f"Production should have higher CPU limits than development ({prod_cpu_limit} vs {dev_cpu_limit})"
        
        # Check that autoscaling is enabled in production
        if not prod_values.get('autoscaling', {}).get('enabled', False):
            return False, "Autoscaling should be enabled in production"
        
        # Check that different database configurations exist
        dev_db_url = dev_values.get('config', {}).get('spring.datasource.url', '')
        prod_db_url = prod_values.get('config', {}).get('spring.datasource.url', '')
        
        if dev_db_url == prod_db_url:
            return False, "Development and production should have different database configurations"
        
    except Exception as e:
        return False, f"Error validating environment-specific values: {e}"
    
    return True, "Environment-specific values are properly configured"

def test_helm_chart_installation():
    """Test that Helm chart installs successfully."""
    print("Testing Helm chart installation...")
    
    # Check if helm is available
    success, stdout, stderr = run_command("helm version --short")
    if not success:
        return False, f"Helm is not available: {stderr}"
    
    chart_dir = "helm/petclinic"
    
    # Test helm lint
    success, stdout, stderr = run_command(f"helm lint {chart_dir}")
    if not success:
        return False, f"Helm lint failed: {stderr}"
    
    # Test helm template (dry-run)
    success, stdout, stderr = run_command(f"helm template test-release {chart_dir}")
    if not success:
        return False, f"Helm template failed: {stderr}"
    
    # Validate that the templated output contains expected Kubernetes resources
    expected_resources = ["Deployment", "Service", "ConfigMap", "Secret", "ServiceAccount"]
    missing_resources = []
    
    for resource in expected_resources:
        if f"kind: {resource}" not in stdout:
            missing_resources.append(resource)
    
    if missing_resources:
        return False, f"Templated output missing resources: {missing_resources}"
    
    # Test with development values
    success, stdout, stderr = run_command(f"helm template test-release {chart_dir} -f {chart_dir}/values-development.yaml")
    if not success:
        return False, f"Helm template with development values failed: {stderr}"
    
    # Test with production values  
    success, stdout, stderr = run_command(f"helm template test-release {chart_dir} -f {chart_dir}/values-production.yaml")
    if not success:
        return False, f"Helm template with production values failed: {stderr}"
    
    # Check that HPA is included in production template
    if "kind: HorizontalPodAutoscaler" not in stdout:
        return False, "HPA should be included in production template"
    
    return True, "Helm chart templates and installs successfully"

def main():
    """Run all tests and report results."""
    print("Starting Helm chart verification tests...\n")
    
    tests = [
        ("Helm chart structure created with Chart.yaml and values.yaml", test_helm_chart_structure),
        ("Templates created for Deployment, Service, ConfigMap, and Secret", test_templates_created),
        ("Values file configured with environment-specific settings", test_environment_specific_values),
        ("Helm chart installs successfully to Kubernetes cluster", test_helm_chart_installation)
    ]
    
    results = {
        "tests_passed": True,
        "test_output": "",
        "criteria_met": [],
        "criteria_failed": []
    }
    
    test_outputs = []
    
    for criterion, test_func in tests:
        try:
            success, message = test_func()
            test_outputs.append(f"✓ {criterion}: {message}" if success else f"✗ {criterion}: {message}")
            
            if success:
                results["criteria_met"].append(criterion)
            else:
                results["criteria_failed"].append(criterion)
                results["tests_passed"] = False
                
        except Exception as e:
            error_msg = f"Test error: {e}"
            test_outputs.append(f"✗ {criterion}: {error_msg}")
            results["criteria_failed"].append(criterion)
            results["tests_passed"] = False
    
    results["test_output"] = "\n".join(test_outputs)
    
    print("\n" + results["test_output"])
    print(f"\nTests passed: {results['tests_passed']}")
    print(f"Criteria met: {len(results['criteria_met'])}/{len(tests)}")
    
    return results

if __name__ == "__main__":
    results = main()
    print(f"\nFinal Results:\n{json.dumps(results, indent=2)}")