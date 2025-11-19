pipeline {
	agent any

	environment {
		SPRING_PROFILES_ACTIVE = 'ci'

		REPO_URL = 'https://github.com/tahajaiti/Packagito.git'

		GIT_CREDS_ID = 'GITHUB-CREDS'
		DOCKER_CREDS_ID = 'DOCKERHUB-CREDS'

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
			when { anyOf { branch 'dev'; branch 'main' } }
			steps {
				script {
					withCredentials([file(credentialsId: 'PACKAGITO_ENV', variable: 'DOTENV_PATH')]) {
						sh """
                     # 1. Use shell variable (escape \$) to fix security warning
                     cp \$DOTENV_PATH .env

                     set -a
                     # 2. Use explicit path (./) to fix 'not found' error
                     . ./.env
                     set +a

                     docker compose up -d --wait mongodb
                   """

						try {
							docker.image('maven:3.9.6-eclipse-temurin-21')
							.inside("-v maven_data:/root/.m2 --network packagito_net") {

								echo "Connected to packagito_net. Running tests..."
								sh 'mvn -Dmaven.repo.local=/root/.m2/repository verify'
							}
						} finally {
							sh 'docker compose down'
						}
					}
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