# Lab 22: Jenkins Pipeline for Application Deployment

## Overview

This lab demonstrates how to create a Jenkins CI/CD pipeline to automate the deployment of a Java Spring Boot application to a Kubernetes cluster.

The pipeline automates the complete workflow from cloning the application source code to deploying the application on Kubernetes.

The application source code and Dockerfile are cloned from:

`https://github.com/Ibrahim-Adel15/Jenkins_App.git`

The Docker image is pushed to Docker Hub:

`moamenothan1/jenkins-app`

The application is deployed to the `jenkins` namespace in Kubernetes.

---

# Objectives

The Jenkins pipeline automates the following tasks:

1. Clone the application source code and Dockerfile.
2. Run unit tests.
3. Build the application.
4. Build a Docker image.
5. Push the Docker image to Docker Hub.
6. Delete the local Docker image.
7. Update the image in `deployment.yaml`.
8. Deploy the application to the Kubernetes cluster.
9. Configure Jenkins post actions for success, failure, and always.

---

# Project Structure

```text
lab22/
├── Jenkins_App/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
│       └── main/
│           └── java/
│               └── com/
│                   └── example/
│                       └── demo/
│                           └── DemoApplication.java
├── Jenkinsfile
├── deployment.yaml
└── screenshots/
    ├── create_credentials.png
    ├── deployment.png
    ├── jenkinsfile.png
    ├── job_create.png
    └── pipeline_finished.png
```

---

# Technologies Used

- Jenkins
- Jenkins Pipeline
- Git
- GitHub
- Maven
- Java 17
- Docker
- Docker Hub
- Kubernetes
- kubectl
- Minikube

---

# Jenkins Pipeline

The pipeline is defined in:

```text
Jenkinsfile
```

The complete Jenkinsfile is:

```groovy
pipeline {
    agent any

    environment {
        IMAGE_NAME = 'moamenothan1/jenkins-app'
        IMAGE_TAG = "${BUILD_NUMBER}"
        FULL_IMAGE = "${IMAGE_NAME}:${IMAGE_TAG}"

        APP_DIR = 'CICD/lab22/Jenkins_App'
        DEPLOYMENT_FILE = 'CICD/lab22/deployment.yaml'
    }

    stages {

        stage('0. Clone Application Source') {
            steps {
                sh '''
                    rm -rf "$APP_DIR"
                    git clone https://github.com/Ibrahim-Adel15/Jenkins_App.git "$APP_DIR"
                '''
            }
        }

        stage('1. Run Unit Test') {
            steps {
                dir("${APP_DIR}") {
                    sh '''
                        docker run --rm \
                            -v "$PWD:/app" \
                            -w /app \
                            maven:3.9-eclipse-temurin-17 \
                            mvn test
                    '''
                }
            }
        }

        stage('2. Build App') {
            steps {
                dir("${APP_DIR}") {
                    sh '''
                        docker run --rm \
                            -v "$PWD:/app" \
                            -w /app \
                            maven:3.9-eclipse-temurin-17 \
                            mvn clean package -DskipTests
                    '''
                }
            }
        }

        stage('3. Build Docker Image') {
            steps {
                dir("${APP_DIR}") {
                    sh 'docker build -t ${FULL_IMAGE} .'
                }
            }
        }

        stage('4. Push Image to Docker Hub') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub-credentials',
                        usernameVariable: 'DOCKERHUB_USER',
                        passwordVariable: 'DOCKERHUB_PASS'
                    )
                ]) {
                    sh '''
                        echo "$DOCKERHUB_PASS" | docker login \
                            -u "$DOCKERHUB_USER" \
                            --password-stdin

                        docker push "$FULL_IMAGE"

                        docker logout
                    '''
                }
            }
        }

        stage('5. Delete Image Locally') {
            steps {
                sh 'docker rmi "$FULL_IMAGE" || true'
            }
        }

        stage('6. Edit Image in deployment.yaml') {
            steps {
                sh '''
                    sed -i "s#image: .*#image: ${FULL_IMAGE}#" "$DEPLOYMENT_FILE"

                    echo "Updated deployment.yaml:"
                    cat "$DEPLOYMENT_FILE"
                '''
            }
        }

        stage('7. Deploy to Kubernetes') {
            steps {
                sh '''
                    kubectl apply -f "$DEPLOYMENT_FILE"
                '''
            }
        }
    }

    post {
        always {
            echo 'Pipeline execution completed.'
        }

        success {
            echo 'SUCCESS: Application deployed successfully.'
        }

        failure {
            echo 'FAILURE: Pipeline failed. Check the console output.'
        }
    }
}
```

---

# Pipeline Stages Explanation

## 0. Clone Application Source

The pipeline clones the application repository:

```text
https://github.com/Ibrahim-Adel15/Jenkins_App.git
```

The source code and Dockerfile are cloned into:

```text
CICD/lab22/Jenkins_App
```

---

## 1. Run Unit Test

The application unit tests are executed using Maven.

```bash
mvn test
```

However, Maven is not executed directly on the Jenkins host. It is executed inside a Docker container using
