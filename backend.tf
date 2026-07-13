terraform {
  backend "s3" {
    bucket         = "hafsa-terraform-state"
    key            = "terraform.tfstate"
    region         = "ap-south-1"
    dynamodb_table = "terraform_lock_78"
    encrypt        = true

  }
}