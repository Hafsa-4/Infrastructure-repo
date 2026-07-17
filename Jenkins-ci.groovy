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
        stage('Terraform Work') {
            steps {
                withVault(configuration: [vaultUrl: 'http://65.2.34.167:8200', vaultCredentialId: 'vault-token'],
                           vaultSecrets: [[path: 'secret/aws-credentials', secretValues: [
                               [envVar: 'AWS_ACCESS_KEY_ID', vaultKey: 'access_key'],
                               [envVar: 'AWS_SECRET_ACCESS_KEY', vaultKey: 'secret_key']]]]) {

                    sh 'terraform init'
                    sh 'terraform fmt -check -recursive'
                    sh 'terraform validate'
                    sh 'terraform plan -out=tfplan'
                }
            }
        }
    }
}