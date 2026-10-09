def call(String imageName, String imageTag, String appDir = 'Jenkins_App') {
    dir(appDir) {
        sh """
            docker build -t ${imageName}:${imageTag} .
        """

        withCredentials([usernamePassword(
            credentialsId: 'dockerhub-credentials',
            usernameVariable: 'DOCKER_USER',
            passwordVariable: 'DOCKER_PASS'
        )]) {
            sh '''
                set +x
                echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
            '''

            sh """
                docker push ${imageName}:${imageTag}
            """

            sh 'docker logout'
        }
    }
}
