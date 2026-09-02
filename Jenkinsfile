pipeline {
    agent any

    tools {
        jdk 'JDK-21'
        maven 'MAVEN'
    }

    environment {
        APP_DIR = 'todo-app'
        DOCKER_IMAGE = 'sanketmahajan/mern-app'
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

        stage('Download Maven Dependencies') {
            steps {
                sh '''
                    set -e

                    cd "$APP_DIR"

                    mvn -B dependency:resolve
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
                    junit allowEmptyResults: true,
                          testResults: 'todo-app/target/surefire-reports/*.xml'
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

                    mvn -B \
                      org.cyclonedx:cyclonedx-maven-plugin:makeAggregateBom
                '''
            }
        }

        stage('Docker Build') {
            steps {
                script {
                    env.IMAGE_TAG = sh(
                        script: 'git rev-parse --short=12 HEAD',
                        returnStdout: true
                    ).trim()

                    env.FULL_IMAGE = "${DOCKER_IMAGE}:${IMAGE_TAG}"
                }

                sh '''
                    set -e

                    echo "Building: $FULL_IMAGE"

                    docker build \
                        -t "$FULL_IMAGE" \
                        -t "$DOCKER_IMAGE:latest" \
                        "$APP_DIR"
                '''
            }
        }

        stage('Trivy Docker Scan') {
            steps {
                sh '''
                    set -e

                    echo "Scanning Docker image: $FULL_IMAGE"

                    trivy image \
                        --scanners vuln \
                        --severity HIGH,CRITICAL \
                        --exit-code 1 \
                        --ignore-unfixed \
                        "$FULL_IMAGE"
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