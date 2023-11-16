terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "4.39.0"
    }
  }
  backend "s3" {
    bucket = "2016-terraform-state-bucket"
    key    = "apprunner.state"
    region = "eu-west-1"
  }
}