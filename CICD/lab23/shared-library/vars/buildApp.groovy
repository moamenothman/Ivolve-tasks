def call(String appDir = 'Jenkins_App') {
    dir(appDir) {
        sh '''
            docker run --rm \
                -u "$(id -u):$(id -g)" \
                -v "$PWD:/app" \
                -w /app \
                maven:3.9-eclipse-temurin-17 \
                mvn clean package -DskipTests
        '''
    }
}
