# IAM configuration for Pet Clinic infrastructure

# Service Account for Cloud SQL Proxy
resource "google_service_account" "cloudsql_proxy" {
  account_id   = "cloudsql-proxy-sa"
  display_name = "Cloud SQL Proxy Service Account"
  description  = "Service account for Cloud SQL Proxy in GKE"
}

# IAM binding for Cloud SQL Proxy
resource "google_project_iam_member" "cloudsql_proxy_client" {
  project = var.project_id
  role    = "roles/cloudsql.client"
  member  = "serviceAccount:${google_service_account.cloudsql_proxy.email}"
}

# Kubernetes Service Account for Workload Identity
resource "google_service_account_iam_member" "workload_identity_binding" {
  service_account_id = google_service_account.cloudsql_proxy.name
  role               = "roles/iam.workloadIdentityUser"
  member             = "serviceAccount:${var.project_id}.svc.id.goog[default/cloudsql-proxy]"
}

# Service Account for monitoring
resource "google_service_account" "monitoring" {
  account_id   = "petclinic-monitoring-sa"
  display_name = "Pet Clinic Monitoring Service Account"
  description  = "Service account for monitoring and logging"
}

# IAM bindings for monitoring service account
resource "google_project_iam_member" "monitoring_roles" {
  for_each = toset([
    "roles/monitoring.metricWriter",
    "roles/logging.logWriter",
    "roles/stackdriver.resourceMetadata.writer"
  ])
  
  project = var.project_id
  role    = each.value
  member  = "serviceAccount:${google_service_account.monitoring.email}"
}

# Custom IAM role for Pet Clinic application
resource "google_project_iam_custom_role" "petclinic_app_role" {
  role_id     = "petclinicAppRole"
  title       = "Pet Clinic Application Role"
  description = "Custom role for Pet Clinic application with minimal required permissions"
  
  permissions = [
    "cloudsql.instances.connect",
    "secretmanager.versions.access",
    "monitoring.timeSeries.create",
    "logging.logEntries.create"
  ]
}

# Bind custom role to GKE service account
resource "google_project_iam_member" "gke_custom_role" {
  project = var.project_id
  role    = google_project_iam_custom_role.petclinic_app_role.id
  member  = "serviceAccount:${google_service_account.gke_service_account.email}"
}