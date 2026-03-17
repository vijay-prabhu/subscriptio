output "endpoint" {
  description = "RDS endpoint"
  value       = aws_db_instance.this.endpoint
}

output "database_name" {
  description = "Database name"
  value       = aws_db_instance.this.db_name
}
