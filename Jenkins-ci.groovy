pipeline {
    agent any
    environment {
        AWS_DEFAULT_REGION = 'ap-south-1'
    }
    stages {
        stage('Checkout') {
            steps { 
                checkout scm 
            }
        }
        stage('Format Check') {
            steps { 
                sh 'terraform fmt -check -recursive' 
            }
        }
        stage('Terraform Work') {
            steps {
                // One wrapper to rule them all: injects AWS credentials into all steps inside
                withVault(configuration: [vaultUrl: 'http://54.221.153.100:8200', vaultCredentialId: 'vault-token'],
                           vaultSecrets: [[path: 'secret/aws-credentials', secretValues: [
                               [envVar: 'AWS_ACCESS_KEY_ID', vaultKey: 'access_key'],
                               [envVar: 'AWS_SECRET_ACCESS_KEY', vaultKey: 'secret_key']]]]) {
                    
                    echo 'Initializing S3 Backend...'
                    sh 'terraform init'
                    
                    echo 'Validating Configuration...'
                    sh 'terraform validate'
                    
                    echo 'Generating Execution Plan...'
                    sh 'terraform plan -out=tfplan'
                }
            }
        }
    }
}