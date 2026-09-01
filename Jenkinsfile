pipeline {
    agent any


    stages {
        stage('Checkout Code') {
            steps {
                git branch: 'main', url: 'https://github.com/sanketM1996/Maven-Selenium-DevSecOps-CiCd.git'
            }
        }

        stage('Stage 2 - Name Here') {
            steps {
                echo "🔍 Step description here"
                // Commands here
            }
        }
 // ➕ Add more stages as needed
    }

    post {
        success {
            echo "✅ Pipeline completed successfully!"
        }
        failure {
            echo "❌ Pipeline failed. Please check the logs."
        }
    }
}