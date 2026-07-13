pipeline {
    agent any
    environment {
        AWS_DEFAULT_REGION = 'ap-south-1'
    }
    stages {
        stage('Checkout') {
            steps { checkout scm }
        }
        stage('Terraform Deploy') {
            steps {
                // Wrap Init, Plan, and Apply with Vault to authorize against S3 and AWS
                withVault(configuration: [vaultUrl: 'http://127.0.0.1:8200', vaultCredentialId: 'vault-token'],
                           vaultSecrets: [[path: 'secret/aws-credentials', secretValues: [
                               [envVar: 'AWS_ACCESS_KEY_ID', vaultKey: 'access_key'],
                               [envVar: 'AWS_SECRET_ACCESS_KEY', vaultKey: 'secret_key']]]]) {
                    
                    echo 'Initializing Backend...'
                    sh 'terraform init'
                    
                    echo 'Generating Plan...'
                    sh 'terraform plan -out=tfplan'
                    
                    echo 'Applying Changes...'
                    sh 'terraform apply -auto-approve tfplan'
                }
            }
        }
    }
}