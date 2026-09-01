pipeline {
    agent any

    tools {
        jdk 'JDK-21'
        maven 'MAVEN'
    }

    environment {
        APP_DIR = 'todo-app'
    }

    stages {

        stage('Checkout Code') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/sanketM1996/Maven-Selenium-DevSecOps-CiCd.git'
            }
        }

        stage('Validate') {
            steps {
                sh '''
                    set -e

                    cd "$APP_DIR"

                    java -version
                    mvn -version
                    test -f pom.xml
                '''
            }
        }

        stage('Security Scans') {
            parallel {

                stage('Gitleaks') {
                    steps {
                        sh '''
                            set -e

                            gitleaks dir . --redact
                        '''
                    }
                }

                stage('Trivy Dependency Scan') {
                    steps {
                        sh '''
                            set -e

                            trivy fs \
                                --scanners vuln \
                                --severity HIGH,CRITICAL \
                                --exit-code 1 \
                                "$APP_DIR"
                        '''
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

        stage('Build & Test') {
            steps {
                sh '''
                    set -e

                    cd "$APP_DIR"

                    mvn -B clean verify
                '''
            }

            post {
                always {
                    junit 'todo-app/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Package') {
            steps {
                sh '''
                    set -e

                    cd "$APP_DIR"

                    mvn -B package -DskipTests
                '''
            }
        }

        stage('SBOM') {
            steps {
                sh '''
                    set -e

                    cd "$APP_DIR"

                    mvn -B org.cyclonedx:cyclonedx-maven-plugin:makeAggregateBom
                '''
            }
        }
    }

    post {
        success {
            echo 'Pipeline completed successfully!'
        }

        failure {
            echo 'Pipeline failed. Please check the logs.'
        }
    }
}