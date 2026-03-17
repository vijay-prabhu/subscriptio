terraform {
  required_version = "~> 1.9"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = var.aws_region
}

locals {
  project     = "subscriptio"
  environment = "dev"
}

module "networking" {
  source = "../../modules/networking"

  project     = local.project
  environment = local.environment
  vpc_cidr    = "10.0.0.0/16"

  public_subnets = {
    a = { cidr = "10.0.1.0/24", az = "${var.aws_region}a" }
    b = { cidr = "10.0.2.0/24", az = "${var.aws_region}b" }
  }

  private_subnets = {
    a = { cidr = "10.0.10.0/24", az = "${var.aws_region}a" }
    b = { cidr = "10.0.11.0/24", az = "${var.aws_region}b" }
  }
}

module "database" {
  source = "../../modules/database"

  project           = local.project
  environment       = local.environment
  subnet_ids        = module.networking.private_subnet_ids
  security_group_id = module.networking.database_security_group_id
  instance_class    = "db.t3.micro"
  database_username = var.db_username
  database_password = var.db_password
}

module "cache" {
  source = "../../modules/cache"

  project           = local.project
  environment       = local.environment
  subnet_ids        = module.networking.private_subnet_ids
  security_group_id = module.networking.app_security_group_id
  node_type         = "cache.t3.micro"
}

module "ecr" {
  source = "../../modules/ecr"

  project     = local.project
  environment = local.environment
}
