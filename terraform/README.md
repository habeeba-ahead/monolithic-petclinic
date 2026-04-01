# Pet Clinic GCP Infrastructure

This Terraform module provisions the necessary GCP infrastructure for the Pet Clinic application.

## Architecture

The infrastructure includes:

- **GKE Cluster**: Managed Kubernetes cluster for running the Pet Clinic application
- **Cloud SQL**: MySQL database instance with private networking
- **VPC Network**: Custom network with subnets and firewall rules
- **IAM**: Service accounts and roles with least privilege access
- **Monitoring**: Integrated logging and monitoring setup

## Prerequisites

1. GCP Project with billing enabled
2. Terraform >= 1.0 installed
3. gcloud CLI configured with appropriate permissions

## Required GCP APIs

The following APIs will be automatically enabled:
- Compute Engine API
- Kubernetes Engine API
- Cloud SQL Admin API
- IAM API
- Cloud Resource Manager API

## Usage

1. Copy the example variables file:
   ```bash
   cp terraform.tfvars.example terraform.tfvars
   ```

2. Update `terraform.tfvars` with your project details:
   ```hcl
   project_id = "your-gcp-project-id"
   region     = "us-central1"
   ```

3. Initialize Terraform:
   ```bash
   terraform init
   ```

4. Plan the deployment:
   ```bash
   terraform plan
   ```

5. Apply the configuration:
   ```bash
   terraform apply
   ```

## Outputs

After successful deployment, the following outputs will be available:
- GKE cluster endpoint and credentials
- Cloud SQL connection details
- Network configuration
- Service account information

## Security Features

- Private GKE cluster with authorized networks
- Cloud SQL with private IP and SSL enforcement
- Workload Identity for secure pod-to-service authentication
- Custom IAM roles with minimal required permissions
- Network policies and firewall rules

## Monitoring and Logging

- Integrated with Google Cloud Operations (formerly Stackdriver)
- Automatic log collection from GKE and Cloud SQL
- Custom metrics and alerting capabilities

## Cleanup

To destroy the infrastructure:
```bash
terraform destroy
```

**Note**: Ensure you have backups of any important data before destroying resources.