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

                            echo "===== GITLEAKS ====="
                            gitleaks detect --source . --redact

                            echo "✅ Gitleaks passed"
                        '''
                    }
                }

                stage('Dependency Scan') {
                    steps {
                        dir('todo-app') {
                            sh '''
                                set -e

                                echo "===== OWASP DEPENDENCY CHECK ====="

                                chmod +x mvnw

                                ./mvnw -version

                                ./mvnw org.owasp:dependency-check-maven:check

                                echo "✅ Dependency scan completed"
                            '''
                        }
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