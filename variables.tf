variable "ec2_instance_type" {
  default = "t3.micro"
  type    = string
}
variable "ec2_ami_id" {
  default = "ami-0a9723306502e2558"
  type    = string
}
variable "vault_token" {
  description = "Vault authentication token"
  type        = string
  sensitive   = true
}