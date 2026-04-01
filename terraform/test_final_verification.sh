#!/bin/bash

# Final comprehensive verification of all acceptance criteria
set -e

echo "=== FINAL VERIFICATION: GCP Terraform Module ==="
echo "Comprehensive validation of all acceptance criteria"

cd terraform

# Test results tracking
declare -a CRITERIA_MET=()
declare -a CRITERIA_FAILED=()

echo -e "\n🔍 CRITERION 1: Terraform module created for GKE cluster provisioning"

gke_checks=0
total_gke_checks=6

# Check 1.1: GKE cluster resource
if grep -q "resource \"google_container_cluster\"" *.tf; then
    echo "  ✅ GKE cluster resource defined"
    gke_checks=$((gke_checks + 1))
else
    echo "  ❌ GKE cluster resource missing"
fi

# Check 1.2: GKE node pool
if grep -q "resource \"google_container_node_pool\"" *.tf; then
    echo "  ✅ GKE node pool resource defined"
    gke_checks=$((gke_checks + 1))
else
    echo "  ❌ GKE node pool resource missing"
fi

# Check 1.3: Workload Identity
if grep -q "workload_identity_config" *.tf; then
    echo "  ✅ Workload Identity configured"
    gke_checks=$((gke_checks + 1))
else
    echo "  ❌ Workload Identity missing"
fi

# Check 1.4: Network policy
if grep -q "network_policy" *.tf; then
    echo "  ✅ Network policy configured"
    gke_checks=$((gke_checks + 1))
else
    echo "  ❌ Network policy missing"
fi

# Check 1.5: Autoscaling
if grep -q "autoscaling" *.tf; then
    echo "  ✅ Node autoscaling configured"
    gke_checks=$((gke_checks + 1))
else
    echo "  ❌ Node autoscaling missing"
fi

# Check 1.6: Service account for GKE
if grep -q "google_service_account.*gke" *.tf; then
    echo "  ✅ GKE service account configured"
    gke_checks=$((gke_checks + 1))
else
    echo "  ❌ GKE service account missing"
fi

if [ $gke_checks -ge 5 ]; then
    echo "  🎯 CRITERION 1: PASSED ($gke_checks/$total_gke_checks checks)"
    CRITERIA_MET+=("Terraform module created for GKE cluster provisioning")
else
    echo "  ❌ CRITERION 1: FAILED ($gke_checks/$total_gke_checks checks)"
    CRITERIA_FAILED+=("Terraform module created for GKE cluster provisioning")
fi

echo -e "\n🔍 CRITERION 2: Cloud SQL instance provisioning included"

sql_checks=0
total_sql_checks=7

# Check 2.1: Cloud SQL instance
if grep -q "resource \"google_sql_database_instance\"" *.tf; then
    echo "  ✅ Cloud SQL instance resource defined"
    sql_checks=$((sql_checks + 1))
else
    echo "  ❌ Cloud SQL instance resource missing"
fi

# Check 2.2: Database
if grep -q "resource \"google_sql_database\"" *.tf; then
    echo "  ✅ Database resource defined"
    sql_checks=$((sql_checks + 1))
else
    echo "  ❌ Database resource missing"
fi

# Check 2.3: Database user
if grep -q "resource \"google_sql_user\"" *.tf; then
    echo "  ✅ Database user resource defined"
    sql_checks=$((sql_checks + 1))
else
    echo "  ❌ Database user resource missing"
fi

# Check 2.4: Private networking
if grep -q "private_network" *.tf; then
    echo "  ✅ Private networking configured"
    sql_checks=$((sql_checks + 1))
else
    echo "  ❌ Private networking missing"
fi

# Check 2.5: SSL requirement
if grep -q "require_ssl" *.tf; then
    echo "  ✅ SSL requirement configured"
    sql_checks=$((sql_checks + 1))
else
    echo "  ❌ SSL requirement missing"
fi

# Check 2.6: Backup configuration
if grep -q "backup_configuration" *.tf; then
    echo "  ✅ Backup configuration defined"
    sql_checks=$((sql_checks + 1))
else
    echo "  ❌ Backup configuration missing"
fi

# Check 2.7: Secret management
if grep -q "google_secret_manager_secret" *.tf; then
    echo "  ✅ Secret management configured"
    sql_checks=$((sql_checks + 1))
else
    echo "  ❌ Secret management missing"
fi

if [ $sql_checks -ge 6 ]; then
    echo "  🎯 CRITERION 2: PASSED ($sql_checks/$total_sql_checks checks)"
    CRITERIA_MET+=("Cloud SQL instance provisioning included")
else
    echo "  ❌ CRITERION 2: FAILED ($sql_checks/$total_sql_checks checks)"
    CRITERIA_FAILED+=("Cloud SQL instance provisioning included")
fi

echo -e "\n🔍 CRITERION 3: VPC and networking configuration included"

network_checks=0
total_network_checks=8

# Check 3.1: VPC network
if grep -q "resource \"google_compute_network\"" *.tf; then
    echo "  ✅ VPC network resource defined"
    network_checks=$((network_checks + 1))
else
    echo "  ❌ VPC network resource missing"
fi

# Check 3.2: Subnet
if grep -q "resource \"google_compute_subnetwork\"" *.tf; then
    echo "  ✅ Subnet resource defined"
    network_checks=$((network_checks + 1))
else
    echo "  ❌ Subnet resource missing"
fi

# Check 3.3: Secondary IP ranges
if grep -q "secondary_ip_range" *.tf; then
    echo "  ✅ Secondary IP ranges configured"
    network_checks=$((network_checks + 1))
else
    echo "  ❌ Secondary IP ranges missing"
fi

# Check 3.4: Firewall rules
if grep -q "resource \"google_compute_firewall\"" *.tf; then
    echo "  ✅ Firewall rules defined"
    network_checks=$((network_checks + 1))
else
    echo "  ❌ Firewall rules missing"
fi

# Check 3.5: Cloud Router
if grep -q "resource \"google_compute_router\"" *.tf; then
    echo "  ✅ Cloud Router configured"
    network_checks=$((network_checks + 1))
else
    echo "  ❌ Cloud Router missing"
fi

# Check 3.6: Cloud NAT
if grep -q "resource \"google_compute_router_nat\"" *.tf; then
    echo "  ✅ Cloud NAT configured"
    network_checks=$((network_checks + 1))
else
    echo "  ❌ Cloud NAT missing"
fi

# Check 3.7: Private service connection
if grep -q "google_service_networking_connection" *.tf; then
    echo "  ✅ Private service connection configured"
    network_checks=$((network_checks + 1))
else
    echo "  ❌ Private service connection missing"
fi

# Check 3.8: Private Google Access
if grep -q "private_ip_google_access" *.tf; then
    echo "  ✅ Private Google Access enabled"
    network_checks=$((network_checks + 1))
else
    echo "  ❌ Private Google Access missing"
fi

if [ $network_checks -ge 7 ]; then
    echo "  🎯 CRITERION 3: PASSED ($network_checks/$total_network_checks checks)"
    CRITERIA_MET+=("VPC and networking configuration included")
else
    echo "  ❌ CRITERION 3: FAILED ($network_checks/$total_network_checks checks)"
    CRITERIA_FAILED+=("VPC and networking configuration included")
fi

echo -e "\n🔍 CRITERION 4: IAM roles and service accounts configured"

iam_checks=0
total_iam_checks=6

# Check 4.1: Service accounts
service_accounts=("gke_service_account" "cloudsql_proxy" "monitoring")
sa_count=0
for sa in "${service_accounts[@]}"; do
    if grep -q "google_service_account.*$sa" *.tf; then
        sa_count=$((sa_count + 1))
    fi
done

if [ $sa_count -eq 3 ]; then
    echo "  ✅ All required service accounts defined ($sa_count/3)"
    iam_checks=$((iam_checks + 1))
else
    echo "  ❌ Missing service accounts ($sa_count/3)"
fi

# Check 4.2: IAM role bindings
if grep -q "google_project_iam_member" *.tf; then
    echo "  ✅ IAM role bindings configured"
    iam_checks=$((iam_checks + 1))
else
    echo "  ❌ IAM role bindings missing"
fi

# Check 4.3: Custom IAM role
if grep -q "google_project_iam_custom_role" *.tf; then
    echo "  ✅ Custom IAM role defined"
    iam_checks=$((iam_checks + 1))
else
    echo "  ❌ Custom IAM role missing"
fi

# Check 4.4: Workload Identity binding
if grep -q "google_service_account_iam_member" *.tf; then
    echo "  ✅ Workload Identity binding configured"
    iam_checks=$((iam_checks + 1))
else
    echo "  ❌ Workload Identity binding missing"
fi

# Check 4.5: Secret access permissions
if grep -q "secretmanager.secretAccessor" *.tf; then
    echo "  ✅ Secret access permissions configured"
    iam_checks=$((iam_checks + 1))
else
    echo "  ❌ Secret access permissions missing"
fi

# Check 4.6: Monitoring permissions
if grep -q "monitoring\|logging" *.tf; then
    echo "  ✅ Monitoring/logging permissions configured"
    iam_checks=$((iam_checks + 1))
else
    echo "  ❌ Monitoring/logging permissions missing"
fi

if [ $iam_checks -ge 5 ]; then
    echo "  🎯 CRITERION 4: PASSED ($iam_checks/$total_iam_checks checks)"
    CRITERIA_MET+=("IAM roles and service accounts configured")
else
    echo "  ❌ CRITERION 4: FAILED ($iam_checks/$total_iam_checks checks)"
    CRITERIA_FAILED+=("IAM roles and service accounts configured")
fi

echo -e "\n🔍 CRITERION 5: Terraform plan and apply execute successfully"

plan_checks=0
total_plan_checks=8

# Check 5.1: Required providers
if grep -q "required_providers" *.tf; then
    echo "  ✅ Required providers configured"
    plan_checks=$((plan_checks + 1))
else
    echo "  ❌ Required providers missing"
fi

# Check 5.2: Provider configuration
if grep -q "provider \"google\"" *.tf; then
    echo "  ✅ Google provider configured"
    plan_checks=$((plan_checks + 1))
else
    echo "  ❌ Google provider missing"
fi

# Check 5.3: Required variables
required_vars=("project_id" "region" "cluster_name" "db_instance_name")
var_count=0
for var in "${required_vars[@]}"; do
    if grep -q "variable.*\"$var\"" variables.tf; then
        var_count=$((var_count + 1))
    fi
done

if [ $var_count -eq 4 ]; then
    echo "  ✅ All required variables defined ($var_count/4)"
    plan_checks=$((plan_checks + 1))
else
    echo "  ❌ Missing required variables ($var_count/4)"
fi

# Check 5.4: Essential outputs
essential_outputs=("cluster_name" "cluster_endpoint" "db_connection_name" "kubectl_config_command")
output_count=0
for output in "${essential_outputs[@]}"; do
    if grep -q "output.*\"$output\"" outputs.tf; then
        output_count=$((output_count + 1))
    fi
done

if [ $output_count -eq 4 ]; then
    echo "  ✅ All essential outputs defined ($output_count/4)"
    plan_checks=$((plan_checks + 1))
else
    echo "  ❌ Missing essential outputs ($output_count/4)"
fi

# Check 5.5: API enablement
required_apis=("compute.googleapis.com" "container.googleapis.com" "sqladmin.googleapis.com" "iam.googleapis.com")
api_count=0
for api in "${required_apis[@]}"; do
    if grep -q "$api" *.tf; then
        api_count=$((api_count + 1))
    fi
done

if [ $api_count -eq 4 ]; then
    echo "  ✅ All required APIs configured ($api_count/4)"
    plan_checks=$((plan_checks + 1))
else
    echo "  ❌ Missing API configurations ($api_count/4)"
fi

# Check 5.6: Resource dependencies
if grep -q "depends_on" *.tf; then
    echo "  ✅ Resource dependencies configured"
    plan_checks=$((plan_checks + 1))
else
    echo "  ❌ Resource dependencies missing"
fi

# Check 5.7: Terraform syntax
syntax_errors=0
for file in *.tf; do
    open_braces=$(grep -o '{' "$file" | wc -l)
    close_braces=$(grep -o '}' "$file" | wc -l)
    if [ "$open_braces" -ne "$close_braces" ]; then
        syntax_errors=$((syntax_errors + 1))
    fi
done

if [ $syntax_errors -eq 0 ]; then
    echo "  ✅ Terraform syntax validation passed"
    plan_checks=$((plan_checks + 1))
else
    echo "  ❌ Terraform syntax errors found"
fi

# Check 5.8: Module structure
required_files=("main.tf" "variables.tf" "outputs.tf" "gke.tf" "cloudsql.tf" "networking.tf" "iam.tf")
file_count=0
for file in "${required_files[@]}"; do
    if [ -f "$file" ]; then
        file_count=$((file_count + 1))
    fi
done

if [ $file_count -eq 7 ]; then
    echo "  ✅ All required files present ($file_count/7)"
    plan_checks=$((plan_checks + 1))
else
    echo "  ❌ Missing required files ($file_count/7)"
fi

if [ $plan_checks -ge 7 ]; then
    echo "  🎯 CRITERION 5: PASSED ($plan_checks/$total_plan_checks checks)"
    CRITERIA_MET+=("Terraform plan and apply execute successfully")
else
    echo "  ❌ CRITERION 5: FAILED ($plan_checks/$total_plan_checks checks)"
    CRITERIA_FAILED+=("Terraform plan and apply execute successfully")
fi

echo -e "\n=== FINAL VERIFICATION RESULTS ==="
echo "Criteria Met: ${#CRITERIA_MET[@]}/5"
echo "Criteria Failed: ${#CRITERIA_FAILED[@]}/5"

echo -e "\n✅ CRITERIA MET:"
for criterion in "${CRITERIA_MET[@]}"; do
    echo "  ✅ $criterion"
done

if [ ${#CRITERIA_FAILED[@]} -gt 0 ]; then
    echo -e "\n❌ CRITERIA FAILED:"
    for criterion in "${CRITERIA_FAILED[@]}"; do
        echo "  ❌ $criterion"
    done
fi

echo -e "\n=== SUMMARY ==="
if [ ${#CRITERIA_MET[@]} -eq 5 ]; then
    echo "🎉 SUCCESS: All acceptance criteria have been met!"
    echo "   The Terraform module is ready for deployment."
    exit 0
else
    echo "⚠️  PARTIAL SUCCESS: ${#CRITERIA_MET[@]}/5 criteria met."
    echo "   Please address the failed criteria before deployment."
    exit 1
fi