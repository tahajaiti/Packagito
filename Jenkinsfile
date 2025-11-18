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
				echo 'Running tests with environment secrets loaded directly...'

				withCredentials([file(credentialsId: 'APP_ENV', variable: 'DOTENV_PATH')]) {

					script {
						// --- DEBUG START ---
						echo "DEBUG: Secret file downloaded to path: ${env.DOTENV_PATH}"

						def envVars = []
						def envContent = readFile(env.DOTENV_PATH)

						envContent.eachLine { line ->
							if (!line.trim().startsWith("#") && line.contains("=")) {
								def parts = line.split("=", 2)
								def varName = parts[0].trim()
								def varValue = parts[1].trim().replaceAll('"', '').replaceAll("'", "")
								envVars << "${varName}=${varValue}"
							}
						}

						echo "DEBUG: Parsed variables count: ${envVars.size()}"
						echo "DEBUG: Variables to inject: ${envVars}"
						// --- DEBUG END ---

						// Now run the tests with the secrets loaded
						withEnv(envVars) {
							sh 'mvn test'
						}
					}
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