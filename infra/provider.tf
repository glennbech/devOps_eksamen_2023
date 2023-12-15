terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "5.25.0"
    }
  }
  backend "s3" {
    bucket = "2016-terraform-state-bucket"
    key    = "apprunner-sensur.state"
    region = "eu-west-1"
  }
}