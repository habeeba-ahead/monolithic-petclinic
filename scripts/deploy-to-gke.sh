#!/bin/bash

set -e

# Configuration
PROJECT_ID=${GCP_PROJECT_ID:-"petclinic-project"}
CLUSTER_NAME=${GKE_CLUSTER_NAME:-"petclinic-cluster"}
CLUSTER_ZONE=${GKE_CLUSTER_ZONE:-"us-central1-a"}
NAMESPACE=${APP_NAMESPACE:-"petclinic"}
IMAGE_TAG=${IMAGE_TAG:-"latest"}

echo "Deploying Pet Clinic to GKE..."
echo "Project: $PROJECT_ID"
echo "Cluster: $CLUSTER_NAME"
echo "Zone: $CLUSTER_ZONE"
echo "Namespace: $NAMESPACE"

# Build the application
echo "Building application..."
./mvnw clean package -DskipTests

# Build Docker image
echo "Building Docker image..."
docker build -t gcr.io/$PROJECT_ID/petclinic:$IMAGE_TAG .

# Push to Google Container Registry
echo "Pushing image to GCR..."
docker push gcr.io/$PROJECT_ID/petclinic:$IMAGE_TAG

# Get GKE credentials
echo "Getting GKE credentials..."
gcloud container clusters get-credentials $CLUSTER_NAME --zone $CLUSTER_ZONE --project $PROJECT_ID

# Update deployment manifest with project ID
sed "s/PROJECT_ID/$PROJECT_ID/g" k8s/deployment.yaml > k8s/deployment-updated.yaml

# Apply Kubernetes manifests
echo "Applying Kubernetes manifests..."
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/activemq-deployment.yaml
kubectl apply -f k8s/deployment-updated.yaml
kubectl apply -f k8s/hpa.yaml

# Wait for deployment to be ready
echo "Waiting for deployment to be ready..."
kubectl wait --for=condition=available --timeout=300s deployment/petclinic-app -n $NAMESPACE
kubectl wait --for=condition=available --timeout=300s deployment/activemq -n $NAMESPACE

# Get service external IP
echo "Getting service external IP..."
kubectl get service petclinic-service -n $NAMESPACE

echo "Deployment completed successfully!"
echo "Run the following command to get the external IP:"
echo "kubectl get service petclinic-service -n $NAMESPACE"

# Clean up temporary file
rm -f k8s/deployment-updated.yaml