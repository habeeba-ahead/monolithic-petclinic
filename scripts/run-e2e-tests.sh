#!/bin/bash

set -e

# Configuration
PROJECT_ID=${GCP_PROJECT_ID:-"petclinic-project"}
CLUSTER_NAME=${GKE_CLUSTER_NAME:-"petclinic-cluster"}
CLUSTER_ZONE=${GKE_CLUSTER_ZONE:-"us-central1-a"}
NAMESPACE=${APP_NAMESPACE:-"petclinic"}

echo "Running E2E tests for Pet Clinic on GKE..."
echo "Project: $PROJECT_ID"
echo "Cluster: $CLUSTER_NAME"
echo "Zone: $CLUSTER_ZONE"
echo "Namespace: $NAMESPACE"

# Get GKE credentials
echo "Getting GKE credentials..."
gcloud container clusters get-credentials $CLUSTER_NAME --zone $CLUSTER_ZONE --project $PROJECT_ID

# Verify cluster connectivity
echo "Verifying cluster connectivity..."
kubectl cluster-info

# Check if application is deployed
echo "Checking application deployment..."
kubectl get deployment petclinic-app -n $NAMESPACE

# Run E2E tests
echo "Running E2E tests..."
cd e2e-tests
mvn test \
  -Dgke.cluster.name=$CLUSTER_NAME \
  -Dgke.cluster.zone=$CLUSTER_ZONE \
  -Dgcp.project.id=$PROJECT_ID \
  -Dapp.namespace=$NAMESPACE

echo "E2E tests completed!"