# Cloud SQL configuration for Pet Clinic database

# Random password for database
resource "random_password" "db_password" {
  length  = 16
  special = true
}

# Cloud SQL instance
resource "google_sql_database_instance" "main" {
  name             = var.db_instance_name
  database_version = var.db_version
  region           = var.region
  
  settings {
    tier = var.db_tier
    
    # Backup configuration
    backup_configuration {
      enabled                        = true
      start_time                     = "02:00"
      point_in_time_recovery_enabled = true
      backup_retention_settings {
        retained_backups = 7
      }
    }
    
    # IP configuration
    ip_configuration {
      ipv4_enabled    = false
      private_network = google_compute_network.vpc.id
      require_ssl     = true
    }
    
    # Maintenance window
    maintenance_window {
      day          = 7
      hour         = 3
      update_track = "stable"
    }
    
    # Database flags
    database_flags {
      name  = "slow_query_log"
      value = "on"
    }
    
    database_flags {
      name  = "general_log"
      value = "on"
    }
    
    # Disk configuration
    disk_autoresize       = true
    disk_autoresize_limit = 100
    disk_size             = 20
    disk_type             = "PD_SSD"
    
    # User labels
    user_labels = {
      environment = var.environment
      application = "petclinic"
    }
  }
  
  deletion_protection = false
  
  depends_on = [
    google_project_service.required_apis,
    google_service_networking_connection.private_vpc_connection
  ]
}

# Private service connection for Cloud SQL
resource "google_compute_global_address" "private_ip_address" {
  name          = "private-ip-address"
  purpose       = "VPC_PEERING"
  address_type  = "INTERNAL"
  prefix_length = 16
  network       = google_compute_network.vpc.id
}

resource "google_service_networking_connection" "private_vpc_connection" {
  network                 = google_compute_network.vpc.id
  service                 = "servicenetworking.googleapis.com"
  reserved_peering_ranges = [google_compute_global_address.private_ip_address.name]
  
  depends_on = [google_project_service.required_apis]
}

# Database
resource "google_sql_database" "petclinic" {
  name     = "petclinic"
  instance = google_sql_database_instance.main.name
}

# Database user
resource "google_sql_user" "petclinic_user" {
  name     = "petclinic"
  instance = google_sql_database_instance.main.name
  password = random_password.db_password.result
}

# Store database password in Secret Manager
resource "google_secret_manager_secret" "db_password" {
  secret_id = "petclinic-db-password"
  
  replication {
    automatic = true
  }
  
  depends_on = [google_project_service.required_apis]
}

resource "google_secret_manager_secret_version" "db_password" {
  secret      = google_secret_manager_secret.db_password.id
  secret_data = random_password.db_password.result
}

# IAM binding for GKE service account to access secrets
resource "google_secret_manager_secret_iam_member" "gke_secret_access" {
  secret_id = google_secret_manager_secret.db_password.secret_id
  role      = "roles/secretmanager.secretAccessor"
  member    = "serviceAccount:${google_service_account.gke_service_account.email}"
}