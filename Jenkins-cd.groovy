pipeline {
    agent any
    environment {
        AWS_DEFAULT_REGION = 'ap-south-1'
    }
    stages {
        stage('Checkout') {
            steps { checkout scm }
        }
        stage('Terraform Init & Plan') {
            steps {
                withVault(configuration: [vaultUrl: 'http://127.0.0.1:8200', vaultCredentialId: 'vault-token'],
                           vaultSecrets: [[path: 'secret/aws-credentials', secretValues: [
                               [envVar: 'AWS_ACCESS_KEY_ID', vaultKey: 'access_key'],
                               [envVar: 'AWS_SECRET_ACCESS_KEY', vaultKey: 'secret_key']]]]) {
                    
                    echo 'Initializing Backend...'
                    // -input=false forces terraform to bypass interactive backend prompts
                    sh 'terraform init -input=false'
                    
                    echo 'Generating Execution Plan...'
                    sh 'terraform plan -out=tfplan'
                }
            }
        }
        stage('Approval Gate') {
            steps {
                // This pauses the pipeline and asks you "Yes or No" in the Jenkins UI
                input message: 'Do you want to apply these infrastructure changes and deploy?', ok: 'Yes, Deploy!'
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