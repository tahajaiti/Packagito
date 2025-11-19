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

		GIT_CREDS_ID = 'git-credentials'
		DOCKER_CREDS_ID = 'dockerhub-credentials'

		DOCKER_IMAGE = 'tahajaiti/packagito'
	}

	options {
		timestamps()
		timeout(time: 15, unit: 'MINUTES')
		skipStagesAfterUnstable()
	}

	stages {
		stage('Checkout') {
			steps {
				checkout scm
			}
		}

		stage('Test & Verify') {
			when {
				anyOf {
					branch 'dev'
					branch 'main'
				}
			}
			steps {
				echo "Running Tests on branch: ${env.BRANCH_NAME}"
				withCredentials([file(credentialsId: 'APP_ENV', variable: 'DOTENV_PATH')]) {
					sh """
                        set -a
                        . ${DOTENV_PATH}
                        set +a

                        mvn ${MAVEN_OPTS} verify
                    """
				}
			}
		}

		stage('Auto-Push to Main') {
			when {
				branch 'dev'
			}
			steps {
				echo 'Tests Passed on Dev. Merging to Main...'
				withCredentials([usernamePassword(credentialsId: GIT_CREDS_ID, usernameVariable: 'GIT_USER', passwordVariable: 'GIT_PASS')]) {
					sh """
                        git config user.email "jenkins@packagito.com"
                        git config user.name "Jenkins CI"

                        git remote set-url origin https://${GIT_USER}:${GIT_PASS}@github.com/tahajaiti/Packagito.git

                        git fetch origin main

                        git checkout -B main origin/main
                        git merge ${env.GIT_COMMIT} --no-ff -m "[CI/JENKINS]: Merge branch 'dev' into main"

                        git push origin main
                    """
				}
			}
		}

		stage('Build & Push Docker Image') {
			when {
				branch 'main'
			}
			steps {
				echo 'Building and Pushing to Docker Hub...'

				withCredentials([usernamePassword(credentialsId: DOCKER_CREDS_ID, usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
					sh """
                       mvn compile jib:build \
                       -DskipTests \
                       -Djib.to.image=docker.io/${DOCKER_IMAGE}:${env.BUILD_NUMBER} \
                       -Djib.to.tags=latest \
                       -Djib.to.auth.username=${DOCKER_USER} \
                       -Djib.to.auth.password=${DOCKER_PASS}
                    """
				}
			}
		}
	}

	post {
		failure {
			echo "[ERROR]: Build FAILED on branch ${env.BRANCH_NAME}."
		}
		success {
			script {
				if (env.BRANCH_NAME == 'dev') {
					echo "[DEV SUCCESS]: Code merged to main."
				} else if (env.BRANCH_NAME == 'main') {
					echo "[MAIN SUCCESS]: Docker Image Deployed: ${DOCKER_IMAGE}:${env.BUILD_NUMBER}"
				}
			}
		}
	}
}