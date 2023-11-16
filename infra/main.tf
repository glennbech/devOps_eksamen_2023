resource "aws_apprunner_service" "service" {
  service_name = "${var.prefix}-runner"

  instance_configuration {
    instance_role_arn = aws_iam_role.role_for_apprunner_service.arn
    cpu = "256"
    memory = "1024"
  }

  source_configuration {

    authentication_configuration {
      access_role_arn = "arn:aws:iam::244530008913:role/service-role/AppRunnerECRAccessRole"
    }
    image_repository {
      image_configuration {
        port = var.port
      }
      image_identifier      = "244530008913.dkr.ecr.eu-west-1.amazonaws.com/${var.ecrRepo}:${var.imageTag}"
      image_repository_type = "ECR"
    }
    auto_deployments_enabled = true
  }
}
