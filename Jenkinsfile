pipeline {
    agent any

    tools {
        jdk 'JDK-21'
        maven 'MAVEN'
    }

    environment {
        APP_DIR = 'todo-app'
        DOCKER_IMAGE = 'sanketmahajan/mavenproject'
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

                    echo "Java:"
                    java -version

                    echo "Maven:"
                    mvn -version

                    test -f pom.xml
                    test -f Dockerfile

                    echo "Project validation successful"
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

                stage('Trivy Filesystem Scan') {
                    steps {
                        sh '''
                            set -e

                            trivy fs \
                                --scanners vuln \
                                --severity HIGH,CRITICAL \
                                --exit-code 1 \
                                --ignore-unfixed \
                                "$APP_DIR"
                        '''
                    }
                }

                stage('Checkov') {
                    steps {
                        sh '''
                            set -e

                            checkov \
                                -f "$APP_DIR/Dockerfile"
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
                    junit(
                        allowEmptyResults: true,
                        testResults: 'todo-app/target/surefire-reports/*.xml'
                    )
                }
            }
        }

        stage('SBOM') {
            steps {
                sh '''
                    set -e

                    cd "$APP_DIR"

                    mvn -B \
                        org.cyclonedx:cyclonedx-maven-plugin:makeAggregateBom

                    echo "SBOM generated:"
                    find target -iname "*bom*"
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

                    cd "$APP_DIR"

                    echo "================================"
                    echo "Building Docker Image"
                    echo "Image: $FULL_IMAGE"
                    echo "================================"

                    docker build \
                        -t "$FULL_IMAGE" \
                        -t "$DOCKER_IMAGE:latest" \
                        .

                    docker images "$DOCKER_IMAGE"
                '''
            }
        }

        stage('Trivy Docker Scan') {
            steps {
                sh '''
                    set -e

                    echo "================================"
                    echo "Scanning Docker Image"
                    echo "$FULL_IMAGE"
                    echo "================================"

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
            echo "Docker image: ${DOCKER_IMAGE}:${IMAGE_TAG}"
        }

        failure {
            echo 'Pipeline failed. Please check the logs.'
        }

        always {
            sh '''
                echo "Cleaning Docker build cache..."
                docker system prune -f || true
            '''
        }
    }
}