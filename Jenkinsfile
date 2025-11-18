pipeline {
	agent {
		docker {
			image 'maven:3.9.6-eclipse-temurin-21'
			args '-v maven_data:/root/.m2 --network packagito_net'
		}
	}

	environment {
		SPRING_PROFILES_ACTIVE = 'ci'
		REPO_URL = 'https://github.com/tahajaiti/Packagito.git'
	}

	options {
		timestamps()
		timeout(time: 15, unit: 'MINUTES')
	}

	stages {
		stage('Checkout') {
			steps {
				checkout scm
			}
		}

		stage('Test & Verify') {
			steps {
				echo 'Running Tests on the DEV branch...'
				withCredentials([file(credentialsId: 'APP_ENV', variable: 'DOTENV_PATH')]) {
					sh """
                        set -a
                        . ${DOTENV_PATH}
                        set +a
                        mvn verify
                    """
				}
			}
		}

		stage('Auto-Push to Main') {
			when {
				branch 'dev'
			}
			steps {
				echo 'Tests Passed. Pushing code to Main...'
				withCredentials([usernamePassword(credentialsId: 'git-credentials', usernameVariable: 'GIT_USER', passwordVariable: 'GIT_PASS')]) {
					sh """
                        git config user.email "jenkins@packagito.com"
                        git config user.name "Jenkins CI"

                        git remote remove origin
                        git remote add origin https://${GIT_USER}:${GIT_PASS}@github.com/${GIT_USER}/Packagito.git

                        git fetch origin

                        git push origin HEAD:main
                    """
				}
			}
		}
	}

	post {
		failure {
			echo 'MERGE BLOCKED: Tests failed on dev. Main was not updated.'
		}
		success {
			echo 'DEPLOYED: Code has been auto-merged to main.'
		}
	}
}