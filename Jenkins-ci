pipeline {
    agent any
    environment {
        AWS_DEFAULT_REGION = 'ap-south-1'
    }
    stages {
        stage('Checkout') {
            steps { checkout scm }
        }
        stage('Format Check') {
            steps { sh 'terraform fmt -check -recursive' }
        }
        stage('Init') {
            steps { sh 'terraform init' }
        }
        stage('Validate') {
            steps { sh 'terraform validate' }
        }
        stage('Plan') {
            steps {
                withVault(configuration: [vaultUrl: 'http://127.0.0.1:8200', vaultCredentialId: 'vault-token'],
                           vaultSecrets: [[path: 'secret/aws-credentials', secretValues: [
                               [envVar: 'AWS_ACCESS_KEY_ID', vaultKey: 'access_key'],
                               [envVar: 'AWS_SECRET_ACCESS_KEY', vaultKey: 'secret_key']]]]) {
                    sh 'terraform plan -out=tfplan'
                }
            }
        }
    }
}