output "ec2_public_ip" {
  value = aws_instance.terra-project.public_ip
}

output "ec2_public_dns" {
  value = aws_instance.terra-project.public_dns
}

output "ec2_private_ip" {
  value = aws_instance.terra-project.private_ip
}
output "ec2_private_dns" {
  value = aws_instance.terra-project.private_dns
}
output "aws_vpc_id" {
 value = aws_vpc.terra-project.id
}
output "aws_subnet_id" {
  value = aws_subnet.terra-project.id
}
output "key_pair_name" {
  value = aws_key_pair.terra-project.key_name
}
output "internet_gateway_id"{
  value = aws_internet_gateway.terra-project.id
}
output "route_table_id" {
  value = aws_route_table.terra-project.id
}
