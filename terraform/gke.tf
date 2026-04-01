# GKE cluster configuration for Pet Clinic application

# Service Account for GKE nodes
resource "google_service_account" "gke_service_account" {
  account_id   = "gke-${var.cluster_name}-sa"
  display_name = "GKE Service Account for ${var.cluster_name}"
  description  = "Service account for GKE cluster nodes"
}

# IAM bindings for GKE service account
resource "google_project_iam_member" "gke_service_account_roles" {
  for_each = toset([
    "roles/logging.logWriter",
    "roles/monitoring.metricWriter",
    "roles/monitoring.viewer",
    "roles/stackdriver.resourceMetadata.writer",
    "roles/storage.objectViewer"
  ])
  
  project = var.project_id
  role    = each.value
  member  = "serviceAccount:${google_service_account.gke_service_account.email}"
}

# GKE Cluster
resource "google_container_cluster" "primary" {
  name     = var.cluster_name
  location = var.region
  
  # Network configuration
  network    = google_compute_network.vpc.name
  subnetwork = google_compute_subnetwork.subnet.name
  
  # Remove default node pool
  remove_default_node_pool = true
  initial_node_count       = 1
  
  # Network policy
  network_policy {
    enabled = true
  }
  
  # IP allocation policy for secondary ranges
  ip_allocation_policy {
    cluster_secondary_range_name  = "gke-pods"
    services_secondary_range_name = "gke-services"
  }
  
  # Master auth configuration
  master_auth {
    client_certificate_config {
      issue_client_certificate = false
    }
  }
  
  # Workload Identity
  workload_identity_config {
    workload_pool = "${var.project_id}.svc.id.goog"
  }
  
  # Addons
  addons_config {
    http_load_balancing {
      disabled = false
    }
    
    horizontal_pod_autoscaling {
      disabled = false
    }
    
    network_policy_config {
      disabled = false
    }
  }
  
  # Maintenance policy
  maintenance_policy {
    daily_maintenance_window {
      start_time = "03:00"
    }
  }
  
  depends_on = [
    google_project_service.required_apis,
    google_compute_subnetwork.subnet
  ]
}

# Node Pool
resource "google_container_node_pool" "primary_nodes" {
  name       = "${var.cluster_name}-node-pool"
  location   = var.region
  cluster    = google_container_cluster.primary.name
  node_count = var.node_count
  
  # Node configuration
  node_config {
    preemptible  = false
    machine_type = var.machine_type
    
    # Service account
    service_account = google_service_account.gke_service_account.email
    oauth_scopes = [
      "https://www.googleapis.com/auth/cloud-platform"
    ]
    
    # Labels
    labels = {
      environment = var.environment
      application = "petclinic"
    }
    
    # Tags
    tags = ["gke-node", "${var.cluster_name}-node"]
    
    # Disk configuration
    disk_size_gb = 30
    disk_type    = "pd-standard"
    
    # Metadata
    metadata = {
      disable-legacy-endpoints = "true"
    }
    
    # Workload Identity
    workload_metadata_config {
      mode = "GKE_METADATA"
    }
  }
  
  # Autoscaling
  autoscaling {
    min_node_count = 1
    max_node_count = 5
  }
  
  # Management
  management {
    auto_repair  = true
    auto_upgrade = true
  }
  
  depends_on = [
    google_project_iam_member.gke_service_account_roles
  ]
}