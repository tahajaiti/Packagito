pipeline {
	agent {
		docker {
			image 'maven:3.9.6-eclipse-temurin-21'
			args '-v maven_data:/root/.m2 --network packagito_net'
		}
	}

	environment {
		//SPRING_PROFILES_ACTIVE = 'dev'
		MAVEN_OPTS = '-Dmaven.repo.local=/root/.m2/repository'
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

		stage('Setup Environment') {
			steps {
				echo 'Verifying Maven and JDK...'
				sh 'java -version'
				sh 'mvn -version'
			}
		}

		stage('Build') {
			steps {
				echo 'Building project...'
				sh 'mvn clean package -DskipTests'
			}
		}

		stage('Test') {
			steps {
				echo 'Running tests...'
				sh 'mvn test'
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