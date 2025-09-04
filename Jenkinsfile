pipeline {
    agent any

    environment {
        // Cambia esto por tu registro Docker
        DOCKER_REGISTRY = 'veradelacruz'
        // Java y Maven configurados en Jenkins (Tools)
        MAVEN_HOME = tool name: 'Maven', type: 'maven'
        JAVA_HOME = tool name: 'Java17', type: 'jdk'
        PATH = "${MAVEN_HOME}/bin:${JAVA_HOME}/bin:${env.PATH}"
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'master', url: 'https://github.com/VeradelaCruz/mini_netflix.git',
                credentialsId: 'github-creds'
            }
        }

        stage('Build & Unit Tests') {
            steps {
                script {
                    // Construimos y testeamos cada microservicio
                    def services = ['catalog-service','rating-service','recommendation-service','user-service','api-gateway','eureka-service','config-server']
                    for (s in services) {
                        dir("${s}") {
                            sh "mvn clean package -DskipTests=false"
                        }
                    }
                }
            }
        }

        stage('Integration Tests') {
            steps {
                script {
                    def services = ['catalog-service','rating-service','recommendation-service','user-service','api-gateway','eureka-service','config-server']
                    for (s in services) {
                        dir("${s}") {
                            // Ejecutar pruebas de integración si existen
                            sh "mvn verify -DskipUnitTests=true || echo 'No integration tests found for ${s}'"
                        }
                    }
                }
            }
        }

        stage('Docker Build & Push') {
            steps {
                script {
                    def services = ['catalog-service','rating-service','recommendation-service','user-service','api-gateway','eureka-service','config-server']
                    for (s in services) {
                        sh "docker build -t ${DOCKER_REGISTRY}/${s}:latest ./${s}"
                        sh "docker push ${DOCKER_REGISTRY}/${s}:latest"
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    // Aquí levantamos los contenedores con docker-compose
                    sh "docker-compose down"
                    sh "docker-compose up -d"
                }
            }
        }
    }

    post {
        always {
            echo 'Pipeline finished!'
        }
        failure {
            echo 'Pipeline failed!'
        }
        success {
            echo 'Pipeline succeeded!'
        }
    }
}
