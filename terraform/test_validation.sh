#!/bin/bash

# Simple validation test for Terraform files
set -e

echo "Testing Terraform file structure..."

# Test 1: Check all required files exist
required_files=("main.tf" "variables.tf" "outputs.tf" "gke.tf" "cloudsql.tf" "networking.tf")

for file in "${required_files[@]}"; do
    if [ ! -f "$file" ]; then
        echo "FAIL: Required file $file not found"
        exit 1
    fi
done

# Test 2: Check for required resources in files
echo "Test 2: Checking required resources..."

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

# Test 3: Check for required variables
if ! grep -q "variable.*project_id" variables.tf; then
    echo "FAIL: project_id variable not found"
    exit 1
fi

if ! grep -q "variable.*region" variables.tf; then
    echo "FAIL: region variable not found"
    exit 1
fi

echo "All validation tests passed!"