```groovy
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

        // =========================
        // 1. CHECKOUT
        // =========================
        stage('Checkout Code') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/sanketM1996/Maven-Selenium-DevSecOps-CiCd.git'
            }
        }


        // =========================
        // 2. VALIDATE
        // =========================
        stage('Validate') {
            steps {
                sh '''
                    set -e

                    cd "$APP_DIR"

                    echo "================================"
                    echo "Java Version"
                    echo "================================"
                    java -version

                    echo "================================"
                    echo "Maven Version"
                    echo "================================"
                    mvn -version

                    test -f pom.xml
                    test -f Dockerfile

                    echo "Project validation successful"
                '''
            }
        }


        // =========================
        // 3. MAVEN DEPENDENCIES
        // =========================
        stage('Download Maven Dependencies') {
            steps {
                sh '''
                    set -e

                    cd "$APP_DIR"

                    echo "Downloading Maven dependencies..."

                    mvn -B dependency:resolve
                '''
            }
        }


        // =========================
        // 4. SECURITY SCANS
        // =========================
        stage('Security Scans') {
            parallel {

                stage('Gitleaks') {
                    steps {
                        sh '''
                            set -e

                            echo "Running Gitleaks..."

                            gitleaks dir . \
                                --redact

                            echo "Gitleaks scan completed"
                        '''
                    }
                }


                stage('Trivy Filesystem Scan') {
                    steps {
                        sh '''
                            set -e

                            echo "Running Trivy filesystem scan..."

                            trivy fs \
                                --scanners vuln \
                                --severity HIGH,CRITICAL \
                                --exit-code 1 \
                                --ignore-unfixed \
                                "$APP_DIR"

                            echo "Trivy filesystem scan completed"
                        '''
                    }
                }


                stage('Checkov') {
                    steps {
                        sh '''
                            set -e

                            echo "Running Checkov..."

                            checkov \
                                -f "$APP_DIR/Dockerfile"

                            echo "Checkov scan completed"
                        '''
                    }
                }
            }
        }


        // =========================
        // 5. BUILD & TEST
        // =========================
        stage('Build & Test') {
            steps {
                sh '''
                    set -e

                    cd "$APP_DIR"

                    echo "Building and testing Maven project..."

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


        // =========================
        // 6. SBOM
        // =========================
        stage('SBOM') {
            steps {
                sh '''
                    set -e

                    cd "$APP_DIR"

                    echo "Generating SBOM..."

                    mvn -B \
                        org.cyclonedx:cyclonedx-maven-plugin:makeAggregateBom

                    echo "SBOM generated:"
                    find target -iname "*bom*"
                '''
            }

            post {
                always {
                    archiveArtifacts(
                        artifacts: 'todo-app/target/**/*bom*.json,todo-app/target/**/*bom*.xml',
                        allowEmptyArchive: true
                    )
                }
            }
        }


        // =========================
        // 7. DOCKER BUILD
        // =========================
        stage('Docker Build') {
            steps {
                script {

                    env.IMAGE_TAG = sh(
                        script: 'git rev-parse --short=12 HEAD',
                        returnStdout: true
                    ).trim()

                    env.FULL_IMAGE =
                        "${DOCKER_IMAGE}:${IMAGE_TAG}"
                }

                sh '''
                    set -e

                    cd "$APP_DIR"

                    echo "================================"
                    echo "Building Docker Image"
                    echo "================================"
                    echo "Image: $FULL_IMAGE"

                    docker build \
                        --pull \
                        -t "$FULL_IMAGE" \
                        -t "$DOCKER_IMAGE:latest" \
                        .

                    echo "================================"
                    echo "Docker Image Created"
                    echo "================================"

                    docker images "$DOCKER_IMAGE"
                '''
            }
        }


        // =========================
        // 8. TRIVY DOCKER SCAN
        // =========================
        stage('Trivy Docker Scan') {
            steps {
                sh '''
                    set -e

                    echo "================================"
                    echo "Scanning Docker Image"
                    echo "================================"
                    echo "Image: $FULL_IMAGE"

                    trivy image \
                        --scanners vuln \
                        --severity HIGH,CRITICAL \
                        --exit-code 1 \
                        --ignore-unfixed \
                        "$FULL_IMAGE"

                    echo "Docker image security scan passed"
                '''
            }
        }
    }


    // =========================
    // POST ACTIONS
    // =========================
    post {

        success {
            echo '================================'
            echo 'PIPELINE SUCCESS'
            echo '================================'
            echo "Docker image: ${DOCKER_IMAGE}:${IMAGE_TAG}"
            echo "Latest tag: ${DOCKER_IMAGE}:latest"
        }

        failure {
            echo '================================'
            echo 'PIPELINE FAILED'
            echo '================================'
            echo 'Please check the Jenkins console logs.'
        }

        always {
            sh '''
                echo "Cleaning Docker build cache..."

                docker system prune -f || true
            '''
        }
    }
}
```
