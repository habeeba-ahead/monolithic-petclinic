#!/bin/bash

# Deployment script for Pet Clinic GCP infrastructure
set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}Pet Clinic GCP Infrastructure Deployment${NC}"
echo "========================================"

# Check if terraform.tfvars exists
if [ ! -f "terraform.tfvars" ]; then
    echo -e "${YELLOW}Warning: terraform.tfvars not found${NC}"
    echo "Please copy terraform.tfvars.example to terraform.tfvars and update with your values"
    echo ""
    echo "Required variables:"
    echo "  - project_id: Your GCP project ID"
    echo "  - region: GCP region (default: us-central1)"
    echo ""
    exit 1
fi

# Check if gcloud is authenticated
if ! gcloud auth list --filter=status:ACTIVE --format="value(account)" | grep -q .; then
    echo -e "${RED}Error: No active gcloud authentication found${NC}"
    echo "Please run: gcloud auth login"
    exit 1
fi

# Get project ID from terraform.tfvars
PROJECT_ID=$(grep "project_id" terraform.tfvars | cut -d'"' -f2)

if [ -z "$PROJECT_ID" ]; then
    echo -e "${RED}Error: project_id not found in terraform.tfvars${NC}"
    exit 1
fi

echo "Project ID: $PROJECT_ID"

# Set the gcloud project
gcloud config set project $PROJECT_ID

echo ""
echo -e "${GREEN}Step 1: Initializing Terraform${NC}"
terraform init

echo ""
echo -e "${GREEN}Step 2: Validating configuration${NC}"
terraform validate

echo ""
echo -e "${GREEN}Step 3: Planning deployment${NC}"
terraform plan -out=tfplan

echo ""
echo -e "${YELLOW}Review the plan above. Do you want to apply these changes? (y/N)${NC}"
read -r response

if [[ "$response" =~ ^([yY][eE][sS]|[yY])$ ]]; then
    echo ""
    echo -e "${GREEN}Step 4: Applying configuration${NC}"
    terraform apply tfplan
    
    echo ""
    echo -e "${GREEN}✅ Deployment completed successfully!${NC}"
    echo ""
    echo "Next steps:"
    echo "1. Configure kubectl:"
    terraform output -raw kubectl_config_command
    echo ""
    echo "2. Verify cluster access:"
    echo "   kubectl get nodes"
    echo ""
    echo "3. Deploy your Pet Clinic application to the cluster"
else
    echo "Deployment cancelled."
    rm -f tfplan
fi