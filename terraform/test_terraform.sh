#!/bin/bash

# Test script for Terraform GCP infrastructure
# This script validates that our Terraform configuration is valid and follows best practices

set -e

echo "Testing Terraform configuration..."

# Test 1: Terraform init should succeed
echo "Test 1: Running terraform init..."
terraform init -backend=false

# Test 2: Terraform validate should pass
echo "Test 2: Running terraform validate..."
terraform validate

# Test 3: Terraform plan should succeed (dry run)
echo "Test 3: Running terraform plan..."
terraform plan -var="project_id=test-project" -var="region=us-central1" -out=tfplan

# Test 4: Check required resources are defined
echo "Test 4: Checking required resources..."

# Check for GKE cluster
if ! grep -q "google_container_cluster" *.tf; then
    echo "FAIL: GKE cluster resource not found"
    exit 1
fi

# Check for Cloud SQL instance
if ! grep -q "google_sql_database_instance" *.tf; then
    echo "FAIL: Cloud SQL instance resource not found"
    exit 1
fi

# Check for VPC network
if ! grep -q "google_compute_network" *.tf; then
    echo "FAIL: VPC network resource not found"
    exit 1
fi

# Check for IAM service account
if ! grep -q "google_service_account" *.tf; then
    echo "FAIL: Service account resource not found"
    exit 1
fi

echo "All tests passed!"