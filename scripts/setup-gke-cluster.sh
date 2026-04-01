#!/bin/bash

set -e

# Configuration
PROJECT_ID=${GCP_PROJECT_ID:-"petclinic-project"}
CLUSTER_NAME=${GKE_CLUSTER_NAME:-"petclinic-cluster"}
CLUSTER_ZONE=${GKE_CLUSTER_ZONE:-"us-central1-a"}
NODE_COUNT=${NODE_COUNT:-3}
MACHINE_TYPE=${MACHINE_TYPE:-"e2-standard-2"}

echo "Setting up GKE cluster for Pet Clinic..."
echo "Project: $PROJECT_ID"
echo "Cluster: $CLUSTER_NAME"
echo "Zone: $CLUSTER_ZONE"
echo "Node count: $NODE_COUNT"
echo "Machine type: $MACHINE_TYPE"

# Set project
gcloud config set project $PROJECT_ID

# Enable required APIs
echo "Enabling required APIs..."
gcloud services enable container.googleapis.com
gcloud services enable containerregistry.googleapis.com

# Create GKE cluster
echo "Creating GKE cluster..."
gcloud container clusters create $CLUSTER_NAME \
  --zone $CLUSTER_ZONE \
  --num-nodes $NODE_COUNT \
  --machine-type $MACHINE_TYPE \
  --enable-autoscaling \
  --min-nodes 1 \
  --max-nodes 10 \
  --enable-autorepair \
  --enable-autoupgrade \
  --enable-ip-alias \
  --enable-stackdriver-kubernetes \
  --addons HorizontalPodAutoscaling,HttpLoadBalancing \
  --disk-size 50GB \
  --disk-type pd-standard

# Get credentials
echo "Getting cluster credentials..."
gcloud container clusters get-credentials $CLUSTER_NAME --zone $CLUSTER_ZONE

# Verify cluster
echo "Verifying cluster..."
kubectl cluster-info
kubectl get nodes

echo "GKE cluster setup completed successfully!"
echo "Cluster: $CLUSTER_NAME"
echo "Zone: $CLUSTER_ZONE"
echo "Nodes: $(kubectl get nodes --no-headers | wc -l)"