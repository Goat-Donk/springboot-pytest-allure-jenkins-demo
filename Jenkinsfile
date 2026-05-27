pipeline {
    agent any

    triggers {
        pollSCM('H/2 * * * *')
    }

    options {
        disableConcurrentBuilds()
    }

    environment {
        APP_PORT = '18080'
        BASE_URL = 'http://127.0.0.1:18080/api'
        PS_BIN = 'C:\\Windows\\System32\\WindowsPowerShell\\v1.0\\powershell.exe'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build Spring Boot App') {
            steps {
                bat 'mvn clean package -DskipTests'
            }
        }

        stage('Install Python Dependencies') {
            steps {
                bat 'python -m pip install -r requirements.txt'
            }
        }

        stage('Run API Tests') {
            steps {
                bat '"%PS_BIN%" -ExecutionPolicy Bypass -File .\\scripts\\run_local_ci.ps1'
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: 'logs/*.log,target/*.jar,allure-results/**', allowEmptyArchive: true

            // 正确写法：不要大括号，不要commandline，参数用逗号分隔
            allure includeProperties: false, jdk: '', results: [[path: 'allure-results']]
        }
    }
}