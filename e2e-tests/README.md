# Pet Clinic GKE End-to-End Tests

This module contains comprehensive end-to-end tests for validating the Pet Clinic application deployment on Google Kubernetes Engine (GKE).

## Test Coverage

### 1. Infrastructure Tests (`GkeInfrastructureTest`)
- Namespace configuration validation
- ConfigMap and resource configuration
- Service and deployment setup
- Health check configuration
- HorizontalPodAutoscaler setup
- Network connectivity validation

### 2. Deployment Tests (`GkeDeploymentE2ETest`)
- Application deployment verification
- Pod health checks and readiness
- Database connectivity testing
- UI functionality validation
- Metrics and logging verification
- Horizontal scaling functionality

### 3. Performance Tests (`GkePerformanceTest`)
- Concurrent request handling
- Response time validation
- Auto-scaling under load
- Memory usage monitoring
- Pod failure recovery

## Prerequisites

1. **GKE Cluster**: A running GKE cluster with sufficient resources
2. **kubectl**: Configured to connect to your GKE cluster
3. **Docker**: For building and pushing container images
4. **Google Cloud SDK**: For GCP authentication and operations

## Environment Setup

### 1. Create GKE Cluster
```bash
# Set environment variables
export GCP_PROJECT_ID="your-project-id"
export GKE_CLUSTER_NAME="petclinic-cluster"
export GKE_CLUSTER_ZONE="us-central1-a"

# Run cluster setup script
./scripts/setup-gke-cluster.sh
```

### 2. Deploy Application
```bash
# Build and deploy the application
./scripts/deploy-to-gke.sh
```

### 3. Run E2E Tests
```bash
# Run all E2E tests
./scripts/run-e2e-tests.sh

# Or run specific test classes
cd e2e-tests
mvn test -Dtest=GkeDeploymentE2ETest
mvn test -Dtest=GkeInfrastructureTest
mvn test -Dtest=GkePerformanceTest
```

## Configuration

### System Properties
The tests can be configured using system properties:

- `gcp.project.id`: GCP project ID
- `gke.cluster.name`: GKE cluster name
- `gke.cluster.zone`: GKE cluster zone
- `app.namespace`: Kubernetes namespace (default: petclinic)

### Test Configuration
Test behavior can be customized in `application-test.properties`:

```properties
# Test timeouts (seconds)
test.timeout.deployment=300
test.timeout.scaling=180
test.timeout.health=60

# Performance test thresholds
test.performance.response.time.max=5000
test.performance.success.rate.min=95.0
```

## Test Execution Order

Tests are executed in a specific order to ensure proper validation:

1. **Infrastructure Tests**: Validate Kubernetes resources and configuration
2. **Deployment Tests**: Verify application deployment and functionality
3. **Performance Tests**: Test scalability and performance characteristics

## Continuous Integration

### GitHub Actions Example
```yaml
name: GKE E2E Tests
on:
  push:
    branches: [main]
  pull_request:
    branches: [main]

jobs:
  e2e-tests:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v2
    
    - name: Setup GCloud
      uses: google-github-actions/setup-gcloud@v0
      with:
        project_id: ${{ secrets.GCP_PROJECT_ID }}
        service_account_key: ${{ secrets.GCP_SA_KEY }}
        export_default_credentials: true
    
    - name: Setup Java
      uses: actions/setup-java@v2
      with:
        java-version: '8'
        distribution: 'adopt'
    
    - name: Deploy to GKE
      run: ./scripts/deploy-to-gke.sh
      env:
        GCP_PROJECT_ID: ${{ secrets.GCP_PROJECT_ID }}
    
    - name: Run E2E Tests
      run: ./scripts/run-e2e-tests.sh
      env:
        GCP_PROJECT_ID: ${{ secrets.GCP_PROJECT_ID }}
```

## Test Reports

Test results are available in:
- `target/surefire-reports/`: JUnit XML reports
- `target/e2e-tests.log`: Detailed test execution logs
- Console output with performance metrics and timing information

## Troubleshooting

### Common Issues

1. **Cluster Connection Issues**
   ```bash
   # Verify cluster connectivity
   kubectl cluster-info
   kubectl get nodes
   ```

2. **Application Not Accessible**
   ```bash
   # Check service status
   kubectl get service petclinic-service -n petclinic
   
   # Check pod status
   kubectl get pods -n petclinic
   ```

3. **Test Timeouts**
   - Increase timeout values in `application-test.properties`
   - Check cluster resources and scaling limits
   - Verify network connectivity

### Debug Mode
Run tests with debug logging:
```bash
mvn test -Dlogging.level.org.springframework.samples.petclinic.e2e=DEBUG
```

## Cleanup

To clean up resources after testing:
```bash
# Delete application resources
kubectl delete namespace petclinic

# Delete GKE cluster (if no longer needed)
gcloud container clusters delete $GKE_CLUSTER_NAME --zone $GKE_CLUSTER_ZONE
```

## Contributing

When adding new E2E tests:

1. Follow the existing test structure and naming conventions
2. Use appropriate test ordering with `@Order` annotations
3. Include proper assertions and error messages
4. Add performance benchmarks where applicable
5. Update this README with new test descriptions