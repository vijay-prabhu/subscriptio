variable "project" {
  description = "Project name"
  type        = string
}

variable "environment" {
  description = "Deployment environment"
  type        = string
}

variable "repositories" {
  description = "Set of ECR repository names"
  type        = set(string)
  default     = ["backend", "admin-dashboard", "customer-portal", "analytics"]
}
