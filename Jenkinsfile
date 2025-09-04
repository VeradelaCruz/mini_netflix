pipeline {
    agent any

    tools {
        maven 'Maven'
        jdk 'Java17'
    }

    environment {
        DOCKERHUB_USER = 'tu_usuario'
        DOCKERHUB_REPO = 'mini_netflix'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main',
                    credentialsId: 'github-creds',
                    url: 'https://github.com/usuario/mini-netflix.git'
            }
        }

        stage('Build & Unit Tests') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('Integration Tests') {
            steps {
                // Levantamos dependencias necesarias para tests
                sh 'docker-compose -f docker-compose.test.yml up -d'
                // Ejecutamos tests de integración
                sh 'mvn verify -Pintegration-tests'
                // Bajamos contenedores de pruebas
                sh 'docker-compose -f docker-compose.test.yml down'
            }
        }

        stage('Docker Build & Push') {
            steps {
                script {
                    def services = ['catalog-service', 'user-service', 'recommendation-service', 'rating-service', 'api-gateway', 'eureka-service', 'config-server']

                    services.each { service ->
                        def imageName = "${DOCKERHUB_USER}/${DOCKERHUB_REPO}-${service}:latest"
                        docker.build(imageName, "./${service}")
                            .push()
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                sh 'docker-compose pull'
                sh 'docker-compose up -d'
            }
        }
    }
}
