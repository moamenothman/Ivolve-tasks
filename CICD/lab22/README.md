# Lab 22: Jenkins Pipeline for Application Deployment

## Overview

This lab demonstrates how to create a Jenkins CI/CD pipeline that automates the process of testing, building, containerizing, pushing, and deploying a Java application to a Kubernetes cluster.

The pipeline performs the following tasks:

1. Clone the application source code from GitHub.
2. Run unit tests.
3. Build the Java application.
4. Build a Docker image using the Dockerfile from the GitHub repository.
5. Push the Docker image to Docker Hub.
6. Delete the local Docker image.
7. Update the Kubernetes `deployment.yaml` with the new image tag.
8. Deploy the application to the Kubernetes cluster.
9. Display pipeline status using Jenkins post actions.

---

## Objectives

- Create a Jenkins Declarative Pipeline.
- Automate Maven unit testing and application building.
- Build Docker images automatically.
- Push Docker images to Docker Hub.
- Use Jenkins credentials securely.
- Dynamically update the Kubernetes deployment image.
- Deploy the application automatically to Kubernetes.
- Verify the deployed application using Kubernetes commands.

---

## Technologies Used

- Jenkins
- Git / GitHub
- Maven
- Java 17
- Docker
- Docker Hub
- Kubernetes
- Minikube
- kubectl
- Spring Boot

---

## Source Code

The application source code and Dockerfile are cloned from:

`https://github.com/Ibrahim-Adel15/Jenkins_App.git`

The application is a Spring Boot application using Maven.

Docker Hub repository:

`moamenothan1/jenkins-app`

---

## Project Structure

```text
.
├── Jenkins_App
│   ├── Dockerfile
│   ├── pom.xml
│   └── src
│       └── main
│           └── java
│               └── com
│                   └── example
│                       └── demo
│                           └── DemoApplication.java
├── Jenkinsfile
├── README.md
├── deployment.yaml
└── screenshots
    ├── create_credentials.png
    ├── deployment.png
    ├── get_pods.png
    ├── jenkinsfile.png
    ├── job_create.png
    └── pipeline_finished.png
```

---

# Jenkins Pipeline

The complete Jenkins pipeline is implemented in the `Jenkinsfile`.

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

# Pipeline Stages

## 0. Clone Application Source

The pipeline removes any
