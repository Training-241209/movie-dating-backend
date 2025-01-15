pipeline{
	agent any
	
	environment {
		DOCKER_IMAGE = 'movie-dating-backend'
		DOCKER_TAG = "${BUILD_NUMBER}"
		DB_CREDS = credentials('DB_CREDENTIALS')
		JWT_SECRET = credentials('JWT_SECRET')
		DB_URL = credentials('DB_URL')
	}
	
	stages {
		stage('Build') {
			steps {
				//Build with maven
				sh 'mvn clean package'
			}
		}
		
		stage('Docker Build') {
			steps {
				script {
					sh "docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} ."
				}
			}
		}
		
		stage('Deploy') {
			steps {
				script {
					//Stop existing container
					sh "docker stop ${DOCKER_IMAGE} || true"
					sh "docker rm ${DOCKER_IMAGE} || true"
					
					//Run new container with environment variables
					sh """
						docker run -d \
						--name ${DOCKER_IMAGE} \
						-p 8081:8080 \
						-e SPRING_DATASOURCE_URL=${DB_URL} \
						-e SPRING_DATASOURCE_USERNAME=${DB_CREDS_USR} \
						-e SPRING_DATASOURCE_PASSWORD=${DB_CREDS_PSW} \
						-e JWT_SECRET=${JWT_SECRET} \
						--restart unless-stopped \
						${DOCKER_IMAGE}:${DOCKER_IMAGE}
					"""
				}
			}
		}
	}
	
	post {
		success {
			echo 'Pipeline succeeded!'
		}
		failure {
			echo 'Pipeline failed!'
		}
	}
}