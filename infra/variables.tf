variable "prefix" {
  type = string
  default = "2016"
}

variable "ecrRepo" {
  type = string
  default = "2016-repo"
}

variable "imageTag" {
  type = string
  default = "latest"
}

variable "port" {
  type = string
  default = "8080"
}

variable "bucket" {
  type = string
  default = "2016-bucket"
}