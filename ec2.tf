resource "aws_instance" "terra-project" {
  ami           = var.ec2_ami_id
  instance_type = var.ec2_instance_type
  key_name      = aws_key_pair.terra-project.key_name
}
resource "aws_key_pair" "terra-project" {
  key_name   = "terra-project"
  public_key = file("terra-project-key.pub")
}

resource "aws_ebs_volume" "terra-project" {
  availability_zone = "ap-south-1a"
  size              = var.ec2_root_storage_size
}
resource "aws_security_group" "terra-project" {
  name        = "terra-project"
  description = "Allow SSH and HTTP"


  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}
resource "aws_vpc" "terra-project" {
  cidr_block = "10.0.0.0/16"
}
resource "aws_subnet" "terra-project" {
  vpc_id     = aws_vpc.terra-project.id
  cidr_block = "10.0.1.0/24"
}
resource "aws_internet_gateway" "terra-project" {
  vpc_id = aws_vpc.terra-project.id
}
resource "aws_route_table" "terra-project" {
  vpc_id = aws_vpc.terra-project.id
  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.terra-project.id
  }
}
resource "aws_route_table_association" "terra-project" {
  subnet_id      = aws_subnet.terra-project.id
  route_table_id = aws_route_table.terra-project.id
}
