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
        stage('Approval Gate') {
            steps {
                input message: 'Deploy these infrastructure changes to AWS?', ok: 'Yes, Deploy!'
            }
        }
        stage('Terraform Apply') {
            steps {
                withVault(configuration: [vaultUrl: 'http://65.2.34.167:8200', vaultCredentialId: 'vault-token'],
                           vaultSecrets: [[path: 'secret/aws-credentials', secretValues: [
                               [envVar: 'AWS_ACCESS_KEY_ID', vaultKey: 'access_key'],
                               [envVar: 'AWS_SECRET_ACCESS_KEY', vaultKey: 'secret_key']]]]) {

                    sh 'terraform init'
                    sh 'terraform apply -auto-approve'
                }
            }
        }
    }
}