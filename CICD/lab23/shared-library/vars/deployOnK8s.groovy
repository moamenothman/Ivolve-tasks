def call(String deploymentFile) {

    sh """
        kubectl apply -f ${deploymentFile}
    """
}
