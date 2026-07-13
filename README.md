# Terraform CI/CD Learning Project with Jenkins, Vault & AWS

## Overview
This is a beginner-friendly DevOps learning project that demonstrates how Terraform, Jenkins, HashiCorp Vault, and AWS work together in a simple CI/CD workflow.

The goal is not to build a production-grade platform, but to understand the core workflow of:
- Writing Terraform code
- Storing it in GitHub
- Validating it through Jenkins CI
- Reading AWS credentials securely from Vault (instead of hardcoding them)
- Deploying AWS infrastructure using Terraform

Region used: **ap-south-1**

## Architecture / Flow
1. Terraform code is written locally and pushed to GitHub
2. Jenkins pulls the code from the repository
3. Jenkins CI pipeline runs `fmt`, `init`, `validate`, and `plan`
4. Jenkins reads AWS credentials from Vault at runtime
5. CD pipeline is triggered manually to run `terraform apply`
6. Terraform provisions AWS resources (VPC, subnet, security group, EC2 instance)

## Tools Used
| Tool | Purpose |
|------|---------|
| Terraform | Infrastructure as Code |
| GitHub | Source control |
| Jenkins | CI/CD pipeline automation |
| HashiCorp Vault | Secure secret storage (AWS credentials) |
| AWS | Cloud provider (EC2, VPC, Security Groups) |

## Repository Structure
terraform-learning-project/
├── main.tf              # Root module / resource wiring
├── ec2.tf                # EC2 instance definition (uses custom VPC subnet & SG)
├── vpc.tf                # Custom VPC, subnet, security group
├── variables.tf          # Input variables
├── outputs.tf            # Output values (e.g. instance public IP)
├── provider.tf           # AWS provider configuration
├── versions.tf           # Terraform & provider version constraints
├── Jenkinsfile           # CI/CD pipeline definition
├── README.md
└── .gitignore
## CI Pipeline (Jenkins)
Triggered on pull request / push:
1. Checkout code from GitHub
2. `terraform fmt -check` — check formatting
3. `terraform init` — download providers
4. `terraform validate` — check syntax
5. `terraform plan` — preview changes

## CD Pipeline (Jenkins)
Triggered **manually**:
1. Jenkins reads AWS credentials from Vault
2. `terraform init` (if needed)
3. `terraform apply` — requires manual approval, never automatic

## Secret Management
AWS credentials (`AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`) are **never** stored in:
- Terraform files
- Jenkinsfile
- GitHub repository

They are stored in HashiCorp Vault and fetched securely by Jenkins during pipeline execution.

## Key Design Notes
- The EC2 instance is launched into a **custom VPC** — not the default VPC — by explicitly referencing `subnet_id` and `vpc_security_group_ids` in `ec2.tf`.
- `terraform apply` is intentionally manual so the change set is always reviewed before deployment.

## Setup Instructions
1. Clone this repository
2. Configure Vault with your AWS credentials
3. Point Jenkins to this GitHub repo (webhook or polling)
4. Run the CI job to validate the code
5. Manually trigger the CD job to deploy
6. Verify resources in the AWS Console (region: `ap-south-1`)

## Future Improvements
- Add remote backend for Terraform state (S3 + DynamoDB state locking)
- Introduce Terraform modules for reusability
- Add a `terraform destroy` workflow with manual approval
- Separate dev/stage/prod environments

## Learning Outcomes
After completing this project, you should be able to explain:
- What Terraform does and how it manages state
- How Jenkins automates CI/CD for infrastructure code
- Why secrets should never be hardcoded, and how Vault solves this
- How AWS resources (VPC, subnet, security group, EC2) fit together
- The difference between automated CI checks and a manual, reviewed CD deployment