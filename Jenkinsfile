pipeline {
	agent any

	environment {
		SPRING_PROFILES_ACTIVE = 'ci'
		REPO_URL = 'https://github.com/tahajaiti/Packagito.git'
		GIT_CREDS_ID = 'GITHUB-CREDS'
		DOCKER_CREDS_ID = 'DOCKERHUB-CREDS'
		DOCKER_IMAGE = 'tahajaiti/packagito'

		MAVEN_CACHE = "${JENKINS_HOME}/maven-cache"
	}

	options {
		timestamps()
		timeout(time: 20, unit: 'MINUTES')
		skipStagesAfterUnstable()
		buildDiscarder(logRotator(numToKeepStr: '10', artifactNumToKeepStr: '5'))
		disableConcurrentBuilds()
	}

	stages {
		stage('Selective Clean') {
			steps {
				script {
					sh '''
                   echo "Cleaning build artifacts, preserving caches..."
                   rm -rf target/
                   rm -rf .env
                   find . -name "*.log" -type f -delete
                '''

					if (currentBuild.number % 20 == 0) {
						echo "Performing deep clean (build #${currentBuild.number})"
						deleteDir()
					}
				}
			}
		}

		stage('Checkout') {
			steps {
				checkout([
					$class: 'GitSCM',
					branches: scm.branches,
					extensions: scm.extensions + [
						[$class: 'CloneOption', noTags: false, shallow: false],
						[$class: 'LocalBranch', localBranch: "**"]
					],
					userRemoteConfigs: scm.userRemoteConfigs
				])
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
				script {
					def mongoRunning = sh(
						script: 'docker ps --filter "name=packagito_db" --filter "status=running" -q',
						returnStdout: true
					).trim()

					if (mongoRunning) {
						echo "MongoDB already running, skipping startup..."
					} else {
						echo "Starting MongoDB..."
						sh 'docker compose up -d --wait mongodb'
					}
				}
			}
		}

		stage('Build & Test') {
			steps {
				script {
					sh """
                   mkdir -p ${MAVEN_CACHE}
                   chmod -R 777 ${MAVEN_CACHE}
                """

					docker.image('maven:3.9.6-eclipse-temurin-21')
					.inside("--network packagito_net -v ${MAVEN_CACHE}:/var/maven/.m2") {
						sh '''
                         mvn clean verify \
                            -Dspring.profiles.active=ci \
                            -Dmaven.repo.local=/var/maven/.m2/repository \
                            -Dmaven.test.failure.ignore=false \
                            --batch-mode \
                            --show-version \
                            -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn
                      '''
					}
				}
			}
		}

		stage('Auto-Push to Main') {
			when { branch 'dev' }
			steps {
				echo 'Tests passed on dev, merging to main...'
				script {
					withCredentials([usernamePassword(credentialsId: GIT_CREDS_ID, usernameVariable: 'GIT_USER', passwordVariable: 'GIT_PASS')]) {
						sh '''
                      git config user.email "jenkins@packagito.com"
                      git config user.name "Jenkins CI"

                      git remote set-url origin https://${GIT_USER}:${GIT_PASS}@github.com/tahajaiti/Packagito.git

                      git fetch origin

                      if git show-ref --verify --quiet refs/remotes/origin/main; then
                          echo "Main branch exists, checking out..."
                          git checkout main 2>/dev/null || git checkout -b main origin/main
                          git reset --hard origin/main
                      else
                          echo "Main branch does not exist, creating from current commit..."
                          git checkout -b main
                      fi

                      git merge origin/dev --no-ff -m "[CI/JENKINS]: Merge dev into main" || echo "Already merged"

                      git push origin main
                   '''
					}
				}
			}
		}

		stage('Build & Push Docker Image') {
			when { branch 'main' }
			steps {
				echo 'Building and pushing Docker image...'
				script {
					sh """
                  	docker build -t ${DOCKER_IMAGE}:${BUILD_NUMBER} -t ${DOCKER_IMAGE}:latest .
                	"""

					withCredentials([usernamePassword(credentialsId: DOCKER_CREDS_ID, usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
						sh '''
                      echo ${DOCKER_PASS} | docker login -u ${DOCKER_USER} --password-stdin
                      docker push ${DOCKER_IMAGE}:${BUILD_NUMBER}
                      docker push ${DOCKER_IMAGE}:latest
                      docker logout
                   '''
					}
				}
			}
		}
	}

	post {
		always {
			script {
				if (currentBuild.number % 10 == 0) {
					echo "Periodic cleanup: stopping Docker services..."
					sh 'docker compose down || true'
				}
			}
		}

		failure {
			echo "[ERROR]: Build FAILED on branch ${env.BRANCH_NAME}"
			sh 'docker compose ps'
		}

		success {
			script {
				if (env.BRANCH_NAME == 'dev') {
					echo "[DEV SUCCESS]: Code merged to main"
				} else if (env.BRANCH_NAME == 'main') {
					echo "[MAIN SUCCESS]: Docker Image Deployed: ${DOCKER_IMAGE}:${env.BUILD_NUMBER}"
				}
			}
		}

		cleanup {
			script {
				if (currentBuild.number % 30 == 0) {
					echo "Cleaning old Maven artifacts..."
					sh """
                   find ${MAVEN_CACHE} -name "*.lastUpdated" -delete || true
                   find ${MAVEN_CACHE} -type d -empty -delete || true
                """
				}
			}
		}
	}
}