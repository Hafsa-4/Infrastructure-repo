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
        stage('Terraform Init & Plan') {
            steps {
                withVault(configuration: [vaultUrl: 'http://54.221.153.100:8200', vaultCredentialId: 'vault-token'],
                           vaultSecrets: [[path: 'secret/aws-credentials', secretValues: [
                               [envVar: 'AWS_ACCESS_KEY_ID', vaultKey: 'access_key'],
                               [envVar: 'AWS_SECRET_ACCESS_KEY', vaultKey: 'secret_key']]]]) {
                    
                    echo 'Initializing Backend...'
                    sh 'terraform init -input=false -migrate-state -force-copy'
                    
                    echo 'Generating Execution Plan...'
                    sh 'terraform plan -out=tfplan'
                }
            }
        }
        stage('Approval Gate') {
            steps {
                // This makes the CD process manual: it pauses here for your approval
                input message: 'Do you want to deploy these infrastructure changes to AWS?', ok: 'Yes, Deploy!'
            }
        }
        stage('Terraform Apply') {
            steps {
                withVault(configuration: [vaultUrl: 'http://127.0.0.1:8200', vaultCredentialId: 'vault-token'],
                           vaultSecrets: [[path: 'secret/aws-credentials', secretValues: [
                               [envVar: 'AWS_ACCESS_KEY_ID', vaultKey: 'access_key'],
                               [envVar: 'AWS_SECRET_ACCESS_KEY', vaultKey: 'secret_key']]]]) {
                    
                    echo 'Applying Infrastructure Changes...'
                    sh 'terraform apply -auto-approve tfplan'
                }
            }
        }
    }
}