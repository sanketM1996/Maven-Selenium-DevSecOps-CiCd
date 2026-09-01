pipeline {
    agent any


    stages {
        stage('Checkout Code') {
            steps {
                git branch: 'main', url: 'https://github.com/sanketM1996/Maven-Selenium-DevSecOps-CiCd.git'
            }
        }
         stage('Validate') {
            steps {
              dir('todo-app') {
               sh '''
                    java -version
                    ./mvnw -version
                    test -f pom.xml
                '''}
            }
        }

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