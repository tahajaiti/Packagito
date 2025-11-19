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
		timeout(time: 20, unit: 'MINUTES')
		skipStagesAfterUnstable()
	}

	stages {
		stage('Clean Workspace') {
			steps {
				deleteDir()
			}
		}

		stage('Checkout') {
			steps {
				checkout scm
			}
		}

		stage('Setup Environment') {
			steps {
				withCredentials([file(credentialsId: 'PACKAGITO_ENV', variable: 'DOTENV_PATH')]) {
					sh 'cp $DOTENV_PATH .env'
				}
			}
		}

		stage('Start Services') {
			steps {
				sh 'docker compose up -d --wait mongodb'
			}
		}

		stage('Build & Test') {
			steps {
				script {
					docker.image('maven:3.9.6-eclipse-temurin-21')
					.inside("--network packagito_net") {
						sh '''
                        mkdir -p ${WORKSPACE}/.m2/repository
                        mvn clean verify \
                            -Dmaven.repo.local=${WORKSPACE}/.m2/repository \
                            -Dspring.profiles.active=ci
                    '''
					}
				}
			}
		}

		stage('Auto-Push to Main') {
			when { branch 'dev' }
			steps {
				echo 'Tests passed on dev, merging to main...'
				withCredentials([usernamePassword(credentialsId: GIT_CREDS_ID, usernameVariable: 'GIT_USER', passwordVariable: 'GIT_PASS')]) {
					sh """
                        git config user.email "jenkins@packagito.com"
                        git config user.name "Jenkins CI"

                        git remote set-url origin https://${GIT_USER}:${GIT_PASS}@github.com/tahajaiti/Packagito.git
                        git fetch origin main
                        git checkout -B main origin/main
                        git merge ${env.GIT_COMMIT} --no-ff -m "[CI/JENKINS]: Merge dev into main"
                        git push origin main
                    """
				}
			}
		}

		stage('Build & Push Docker Image') {
			when { branch 'main' }
			steps {
				echo 'Building and pushing Docker image...'
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
		failure { echo "[ERROR]: Build FAILED on branch ${env.BRANCH_NAME}" }
		success {
			script {
				if (env.BRANCH_NAME == 'dev') {
					echo "[DEV SUCCESS]: Code merged to main"
				} else if (env.BRANCH_NAME == 'main') {
					echo "[MAIN SUCCESS]: Docker Image Deployed: ${DOCKER_IMAGE}:${env.BUILD_NUMBER}"
				}
			}
		}
	}
}
