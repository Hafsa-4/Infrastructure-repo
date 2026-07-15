provider "aws" {
  region = "ap-south-1"
}
provider "vault" {
  address = "http://54.221.153.100:8200"
  token   = var.vault_token
}