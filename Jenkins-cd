pipeline {
    agent any
    environment {
        AWS_DEFAULT_REGION = 'ap-south-1'
    }
    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/Hafsa-4/Infrastructure-repo.git'
            }
        }
        stage('Init') {
            steps { sh 'terraform init' }
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
        stage('Approval') {
            steps { input message: 'Approve deploy to AWS?', ok: 'Deploy' }
        }
        stage('Apply') {
            steps {
                withVault(configuration: [vaultUrl: 'http://127.0.0.1:8200', vaultCredentialId: 'vault-token'],
                           vaultSecrets: [[path: 'secret/aws-credentials', secretValues: [
                               [envVar: 'AWS_ACCESS_KEY_ID', vaultKey: 'access_key'],
                               [envVar: 'AWS_SECRET_ACCESS_KEY', vaultKey: 'secret_key']]]]) {
                    sh 'terraform apply -auto-approve tfplan'
                }
            }
        }
    }
}