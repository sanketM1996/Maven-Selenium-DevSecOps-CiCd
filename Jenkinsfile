pipeline {
    agent any

    stages {

        stage('Checkout Code') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/sanketM1996/Maven-Selenium-DevSecOps-CiCd.git'
            }
        }

        stage('Validate') {
            steps {
                dir('todo-app') {
                    sh '''
                        set -e

                        echo "===== JAVA ====="
                        java -version

                        echo "===== MAVEN ====="
                        chmod +x mvnw
                        ./mvnw -version

                        echo "===== POM ====="
                        test -f pom.xml

                        echo "✅ Validation successful"
                    '''
                }
            }
        }

       stage('Security Scans') {
    parallel {

        stage('Gitleaks') {
            steps {
                sh '''
                    set -e
                    gitleaks detect --source . --redact
                '''
            }
        }

        stage('Trivy Dependency Scan') {
            steps {
                sh '''
                    set -e

                    echo "===== TRIVY DEPENDENCY SCAN ====="

                    trivy fs \
                        --scanners vuln \
                        --severity HIGH,CRITICAL \
                        --exit-code 1 \
                        todo-app/

                    echo "✅ Dependency vulnerability scan passed"
                '''
            }
        }

        stage('Checkov') {
            steps {
                sh '''
                    set -e

                    echo "===== CHECKOV ====="

                    checkov -d .

                    echo "✅ Checkov passed"
                '''
            }
        }
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