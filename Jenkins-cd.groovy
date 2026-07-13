stage('Terraform Init & Plan') {
            steps {
                withVault(configuration: [vaultUrl: 'http://127.0.0.1:8200', vaultCredentialId: 'vault-token'],
                           vaultSecrets: [[path: 'secret/aws-credentials', secretValues: [
                               [envVar: 'AWS_ACCESS_KEY_ID', vaultKey: 'access_key'],
                               [envVar: 'AWS_SECRET_ACCESS_KEY', vaultKey: 'secret_key']]]]) {
                    
                    echo 'Initializing Backend...'
                    // Added -migrate-state to auto-approve moving the state to S3
                    sh 'terraform init -input=false -migrate-state'
                    
                    echo 'Generating Execution Plan...'
                    sh 'terraform plan -out=tfplan'
                }
            }
        }