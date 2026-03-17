output "vpc_id" {
  description = "VPC ID"
  value       = module.networking.vpc_id
}

output "database_endpoint" {
  description = "RDS endpoint"
  value       = module.database.endpoint
  sensitive   = true
}

output "ecr_urls" {
  description = "ECR repository URLs"
  value       = module.ecr.repository_urls
}
