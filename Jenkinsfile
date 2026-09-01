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

                        java -version
                        chmod +x mvnw
                        ./mvnw -version
                        test -f pom.xml
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

                stage('Dependency Scan') {
                    steps {
                        dir('todo-app') {
                            sh '''
                                set -e

                                echo ">>> Maven"
                                mvn -version

                                echo ">>> OWASP Dependency Check"
                                mvn org.owasp:dependency-check-maven:check

                                echo "✅ Dependency scan completed"
                            '''
                        }
                    }
                }

                stage('Checkov') {
                    steps {
                        sh '''
                            set -e
                            checkov -d .
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