variable "project" {
  description = "Project name"
  type        = string
}

variable "environment" {
  description = "Deployment environment"
  type        = string
}

variable "aws_region" {
  description = "AWS region"
  type        = string
}

variable "private_subnet_ids" {
  description = "Private subnet IDs for tasks"
  type        = list(string)
}

variable "app_security_group_id" {
  description = "Security group for app containers"
  type        = string
}

variable "execution_role_arn" {
  description = "ECS task execution role ARN"
  type        = string
}

variable "task_role_arn" {
  description = "ECS task role ARN"
  type        = string
}

variable "ecr_urls" {
  description = "Map of service name to ECR repository URL"
  type        = map(string)
}

variable "database_url" {
  description = "JDBC database URL"
  type        = string
  sensitive   = true
}

variable "backend_cpu" {
  description = "CPU units for backend task"
  type        = string
  default     = "512"
}

variable "backend_memory" {
  description = "Memory (MB) for backend task"
  type        = string
  default     = "1024"
}

variable "backend_desired_count" {
  description = "Desired count for backend service"
  type        = number
  default     = 1
}
