def call(String appDir = 'Jenkins_App') {

    dir(appDir) {
        sh '''
    if [ -d "$APP_DIR" ]; then
        chmod -R u+w "$APP_DIR" 2>/dev/null || true
        rm -rf "$APP_DIR"
    fi

    git clone https://github.com/Ibrahim-Adel15/Jenkins_App.git "$APP_DIR"
'''
    }
}
