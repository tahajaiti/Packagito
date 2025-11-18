pipeline {
	agent {
		docker {
			image 'maven:3.9.6-eclipse-temurin-21'
			args '-v maven_data:/root/.m2 --network packagito_net'
		}
	}

	environment {
		SPRING_PROFILES_ACTIVE = 'ci'
		MAVEN_OPTS = '-Dmaven.repo.local=/root/.m2/repository'
		DOTENV_PATH = credentials('APP_ENV')
	}

	options {
		timestamps()
		timeout(time: 30, unit: 'MINUTES')
		ansiColor('xterm')
		buildDiscarder(logRotator(numToKeepStr: '5'))
	}

	stages {

		stage('Checkout') {
			steps {
				echo 'Checking out code...'
				checkout scm
			}
		}

		stage('Build') {
			steps {
				echo 'Building project (Skipping tests)...'
				sh 'mvn clean package -DskipTests'
			}
		}

		stage('Test') {
			steps {
				echo 'Running tests with environment secrets...'
				sh '''
                    set -a
                    . $DOTENV_PATH
                    set +a
                    mvn test
                '''
				}
			}
			post {
				always {
					junit '**/target/surefire-reports/*.xml'
				}
			}
		}

		stage('Archive Artifacts') {
			steps {
				echo 'Archiving JAR and reports...'
				archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
				archiveArtifacts artifacts: 'target/surefire-reports/*.xml', fingerprint: true
			}
		}

	}

	post {
		success {
			echo '[SUCCESS] :Build and tests passed.'
		}
		failure {
			echo '[ERROR]: Build or tests failed.'
		}
		always {
			cleanWs()
		}
	}
}