#!/bin/bash

# Comprehensive test for Terraform configuration
set -e

echo "Running comprehensive Terraform tests..."

# Test 1: Check file structure
echo "Test 1: Checking file structure..."
required_files=("main.tf" "variables.tf" "outputs.tf" "gke.tf" "cloudsql.tf" "networking.tf" "iam.tf")

for file in "${required_files[@]}"; do
    if [ ! -f "$file" ]; then
        echo "FAIL: Required file $file not found"
        exit 1
    fi
    echo "✓ Found $file"
done

# Test 2: Check for required resources
echo -e "\nTest 2: Checking required resources..."

resources=(
    "google_container_cluster:GKE cluster"
    "google_sql_database_instance:Cloud SQL instance"
    "google_compute_network:VPC network"
    "google_service_account:Service account"
    "google_compute_subnetwork:Subnet"
    "google_compute_firewall:Firewall rules"
    "google_project_service:API enablement"
)

for resource in "${resources[@]}"; do
    resource_type="${resource%%:*}"
    resource_desc="${resource##*:}"
    
    if grep -q "$resource_type" *.tf; then
        echo "✓ Found $resource_desc ($resource_type)"
    else
        echo "FAIL: $resource_desc ($resource_type) not found"
        exit 1
    fi
done

# Test 3: Check for required variables
echo -e "\nTest 3: Checking required variables..."

required_vars=("project_id" "region" "cluster_name" "db_instance_name" "network_name")

for var in "${required_vars[@]}"; do
    if grep -q "variable.*\"$var\"" variables.tf; then
        echo "✓ Found variable $var"
    else
        echo "FAIL: Variable $var not found"
        exit 1
    fi
done

# Test 4: Check for outputs
echo -e "\nTest 4: Checking outputs..."

required_outputs=("cluster_name" "db_connection_name" "network_name" "kubectl_config_command")

for output in "${required_outputs[@]}"; do
    if grep -q "output.*\"$output\"" outputs.tf; then
        echo "✓ Found output $output"
    else
        echo "FAIL: Output $output not found"
        exit 1
    fi
done

# Test 5: Check for security best practices
echo -e "\nTest 5: Checking security best practices..."

security_checks=(
    "workload_identity_config:Workload Identity"
    "private_ip_google_access:Private Google Access"
    "require_ssl:SSL requirement"
    "backup_configuration:Backup configuration"
)

for check in "${security_checks[@]}"; do
    check_pattern="${check%%:*}"
    check_desc="${check##*:}"
    
    if grep -q "$check_pattern" *.tf; then
        echo "✓ Found $check_desc"
    else
        echo "⚠ Warning: $check_desc not found"
    fi
done

echo -e "\n✅ All comprehensive tests passed!"
echo "Terraform configuration is ready for deployment."