# Pet Clinic Helm Chart

This Helm chart deploys the Spring Pet Clinic application to Kubernetes.

## Prerequisites

- Kubernetes 1.19+
- Helm 3.0+

## Installing the Chart

To install the chart with the release name `petclinic`:

```bash
helm install petclinic ./helm/petclinic
```

## Uninstalling the Chart

To uninstall/delete the `petclinic` deployment:

```bash
helm uninstall petclinic
```

## Configuration

The following table lists the configurable parameters of the Pet Clinic chart and their default values.

| Parameter | Description | Default |
|-----------|-------------|---------|
| `replicaCount` | Number of replicas | `1` |
| `image.repository` | Image repository | `petclinic` |
| `image.tag` | Image tag | `latest` |
| `image.pullPolicy` | Image pull policy | `IfNotPresent` |
| `service.type` | Service type | `ClusterIP` |
| `service.port` | Service port | `8080` |
| `environment` | Environment name | `development` |
| `resources.limits.cpu` | CPU limit | `500m` |
| `resources.limits.memory` | Memory limit | `512Mi` |
| `resources.requests.cpu` | CPU request | `250m` |
| `resources.requests.memory` | Memory request | `256Mi` |

## Environment-Specific Deployments

### Development
```bash
helm install petclinic-dev ./helm/petclinic \
  --set environment=development \
  --set replicaCount=1
```

### Staging
```bash
helm install petclinic-staging ./helm/petclinic \
  --set environment=staging \
  --set replicaCount=2 \
  --set image.tag=v1.2.3
```

### Production
```bash
helm install petclinic-prod ./helm/petclinic \
  --set environment=production \
  --set replicaCount=3 \
  --set image.tag=v1.2.3 \
  --set resources.limits.cpu=1000m \
  --set resources.limits.memory=1Gi
```

## Values Files

You can also use values files for different environments:

```bash
# Create environment-specific values files
helm install petclinic-prod ./helm/petclinic -f values-production.yaml
```

Example `values-production.yaml`:
```yaml
environment: production
replicaCount: 3
image:
  tag: v1.2.3
resources:
  limits:
    cpu: 1000m
    memory: 1Gi
  requests:
    cpu: 500m
    memory: 512Mi
```

## Configuration Management

The chart creates:
- **ConfigMap**: Contains application configuration
- **Secret**: Contains sensitive data like database passwords
- **Deployment**: Runs the Pet Clinic application
- **Service**: Exposes the application
- **ServiceAccount**: For pod security

## Health Checks

The deployment includes:
- **Liveness Probe**: Checks `/manage/health` endpoint
- **Readiness Probe**: Checks `/manage/health` endpoint

## Customization

To customize the deployment, you can:

1. Override values using `--set` flags
2. Create custom values files
3. Modify the chart templates for advanced customization

## Testing

To test the chart without deploying:

```bash
# Lint the chart
helm lint ./helm/petclinic

# Render templates
helm template petclinic ./helm/petclinic

# Dry run
helm install petclinic ./helm/petclinic --dry-run
```