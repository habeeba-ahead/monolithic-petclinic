#!/bin/bash

# Test script to verify all acceptance criteria for GCP Terraform module
set -e

echo "=== Testing Terraform Module for GCP Infrastructure ==="
echo "Verifying all acceptance criteria..."

# Initialize test results
TESTS_PASSED=0
TESTS_FAILED=0
FAILED_CRITERIA=()
PASSED_CRITERIA=()

# Function to log test results
log_test() {
    local test_name="$1"
    local result="$2"
    
    if [ "$result" = "PASS" ]; then
        echo "✅ PASS: $test_name"
        TESTS_PASSED=$((TESTS_PASSED + 1))
        PASSED_CRITERIA+=("$test_name")
    else
        echo "❌ FAIL: $test_name"
        TESTS_FAILED=$((TESTS_FAILED + 1))
        FAILED_CRITERIA+=("$test_name")
    fi
}

# Change to terraform directory
cd terraform

echo -e "\n1. Testing Terraform module structure and initialization..."

# Test: Terraform init
if terraform init -backend=false > /dev/null 2>&1; then
    log_test "Terraform initialization" "PASS"
else
    log_test "Terraform initialization" "FAIL"
fi

# Test: Terraform validate
if terraform validate > /dev/null 2>&1; then
    log_test "Terraform configuration validation" "PASS"
else
    log_test "Terraform configuration validation" "FAIL"
fi

echo -e "\n2. Testing GKE cluster provisioning configuration..."

# Check for GKE cluster resource
if grep -q "resource \"google_container_cluster\"" *.tf; then
    log_test "GKE cluster resource defined" "PASS"
else
    log_test "GKE cluster resource defined" "FAIL"
fi

# Check for GKE node pool
if grep -q "resource \"google_container_node_pool\"" *.tf; then
    log_test "GKE node pool resource defined" "PASS"
else
    log_test "GKE node pool resource defined" "FAIL"
fi

# Check for GKE service account
if grep -q "google_service_account.*gke" *.tf; then
    log_test "GKE service account configured" "PASS"
else
    log_test "GKE service account configured" "FAIL"
fi

# Check for Workload Identity
if grep -q "workload_identity_config" *.tf; then
    log_test "GKE Workload Identity configured" "PASS"
else
    log_test "GKE Workload Identity configured" "FAIL"
fi

echo -e "\n3. Testing Cloud SQL instance provisioning..."

# Check for Cloud SQL instance
if grep -q "resource \"google_sql_database_instance\"" *.tf; then
    log_test "Cloud SQL instance resource defined" "PASS"
else
    log_test "Cloud SQL instance resource defined" "FAIL"
fi

# Check for database
if grep -q "resource \"google_sql_database\"" *.tf; then
    log_test "Cloud SQL database resource defined" "PASS"
else
    log_test "Cloud SQL database resource defined" "FAIL"
fi

# Check for database user
if grep -q "resource \"google_sql_user\"" *.tf; then
    log_test "Cloud SQL user resource defined" "PASS"
else
    log_test "Cloud SQL user resource defined" "FAIL"
fi

# Check for private IP configuration
if grep -q "private_network" *.tf; then
    log_test "Cloud SQL private networking configured" "PASS"
else
    log_test "Cloud SQL private networking configured" "FAIL"
fi

# Check for backup configuration
if grep -q "backup_configuration" *.tf; then
    log_test "Cloud SQL backup configuration" "PASS"
else
    log_test "Cloud SQL backup configuration" "FAIL"
fi

echo -e "\n4. Testing VPC and networking configuration..."

# Check for VPC network
if grep -q "resource \"google_compute_network\"" *.tf; then
    log_test "VPC network resource defined" "PASS"
else
    log_test "VPC network resource defined" "FAIL"
fi

# Check for subnet
if grep -q "resource \"google_compute_subnetwork\"" *.tf; then
    log_test "Subnet resource defined" "PASS"
else
    log_test "Subnet resource defined" "FAIL"
fi

# Check for secondary IP ranges for GKE
if grep -q "secondary_ip_range" *.tf; then
    log_test "GKE secondary IP ranges configured" "PASS"
else
    log_test "GKE secondary IP ranges configured" "FAIL"
fi

# Check for firewall rules
if grep -q "resource \"google_compute_firewall\"" *.tf; then
    log_test "Firewall rules defined" "PASS"
else
    log_test "Firewall rules defined" "FAIL"
fi

# Check for Cloud NAT
if grep -q "resource \"google_compute_router_nat\"" *.tf; then
    log_test "Cloud NAT configured" "PASS"
else
    log_test "Cloud NAT configured" "FAIL"
fi

# Check for private service connection
if grep -q "google_service_networking_connection" *.tf; then
    log_test "Private service connection configured" "PASS"
else
    log_test "Private service connection configured" "FAIL"
fi

echo -e "\n5. Testing IAM roles and service accounts..."

# Check for service accounts
service_accounts=("gke_service_account" "cloudsql_proxy" "monitoring")
for sa in "${service_accounts[@]}"; do
    if grep -q "google_service_account.*$sa" *.tf; then
        log_test "Service account '$sa' defined" "PASS"
    else
        log_test "Service account '$sa' defined" "FAIL"
    fi
done

# Check for IAM role bindings
if grep -q "google_project_iam_member" *.tf; then
    log_test "IAM role bindings configured" "PASS"
else
    log_test "IAM role bindings configured" "FAIL"
fi

# Check for custom IAM role
if grep -q "google_project_iam_custom_role" *.tf; then
    log_test "Custom IAM role defined" "PASS"
else
    log_test "Custom IAM role defined" "FAIL"
fi

# Check for Secret Manager configuration
if grep -q "google_secret_manager_secret" *.tf; then
    log_test "Secret Manager configured" "PASS"
else
    log_test "Secret Manager configured" "FAIL"
fi

echo -e "\n6. Testing Terraform plan execution..."

# Test terraform plan with sample variables
if terraform plan -var="project_id=test-project-123" -var="region=us-central1" -out=tfplan > /dev/null 2>&1; then
    log_test "Terraform plan execution successful" "PASS"
else
    log_test "Terraform plan execution successful" "FAIL"
fi

echo -e "\n7. Testing required APIs enablement..."

# Check for required APIs
required_apis=("compute.googleapis.com" "container.googleapis.com" "sqladmin.googleapis.com" "iam.googleapis.com")
for api in "${required_apis[@]}"; do
    if grep -q "$api" *.tf; then
        log_test "API '$api' enablement configured" "PASS"
    else
        log_test "API '$api' enablement configured" "FAIL"
    fi
done

echo -e "\n8. Testing outputs configuration..."

# Check for essential outputs
essential_outputs=("cluster_name" "cluster_endpoint" "db_connection_name" "network_name" "kubectl_config_command")
for output in "${essential_outputs[@]}"; do
    if grep -q "output.*\"$output\"" outputs.tf; then
        log_test "Output '$output' defined" "PASS"
    else
        log_test "Output '$output' defined" "FAIL"
    fi
done

echo -e "\n9. Testing security best practices..."

# Check for security configurations
security_features=("require_ssl" "workload_identity_config" "private_ip_google_access" "deletion_protection")
for feature in "${security_features[@]}"; do
    if grep -q "$feature" *.tf; then
        log_test "Security feature '$feature' configured" "PASS"
    else
        log_test "Security feature '$feature' configured" "FAIL"
    fi
done

echo -e "\n=== TEST SUMMARY ==="
echo "Tests Passed: $TESTS_PASSED"
echo "Tests Failed: $TESTS_FAILED"

if [ $TESTS_FAILED -eq 0 ]; then
    echo "🎉 ALL ACCEPTANCE CRITERIA MET!"
    exit 0
else
    echo "❌ Some acceptance criteria not met."
    echo "Failed criteria:"
    for criteria in "${FAILED_CRITERIA[@]}"; do
        echo "  - $criteria"
    done
    exit 1
fi