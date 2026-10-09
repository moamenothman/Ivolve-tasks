def call(String imageName, String imageTag, String appDir = 'Jenkins_App') {

    dir(appDir) {
        sh """
            docker build -t ${imageName}:${imageTag} .
        """
    }
}
