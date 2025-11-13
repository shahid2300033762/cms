pipeline {
  agent any

  options {
    timestamps()
    buildDiscarder(logRotator(numToKeepStr: '20'))
    timeout(time: 30, unit: 'MINUTES')
  }

  environment {
    PROJECT_NAME = 'ems-main'
    FRONTEND_DIR = 'ems-main/storyspark-content-hub-main'
    BACKEND_DIR  = 'ems-main/backend'
    COMPOSE_FILE = 'docker-compose.yml'
    FRONTEND_PORT = '8081'
    BACKEND_PORT  = '8082'

    DOCKER_USER = 'YOUR_DOCKERHUB_USERNAME'
    DOCKER_PASS = 'YOUR_DOCKERHUB_PASSWORD'
  }

  stages {

    /* ------------------------------ */
    stage('Diagnostics') {
      steps {
        script {
          if (isUnix()) {
            sh 'echo ==== Jenkins diagnostics ===='
            sh 'whoami && pwd'
            sh 'docker --version || true'
            sh 'docker compose version || true'
          } else {
            bat 'echo ==== Jenkins diagnostics ===='
            bat 'whoami'
            bat 'cd'
            bat 'docker --version'
            bat 'docker compose version'
          }
        }
      }
    }

    /* ------------------------------ */
    stage('Pre-clean (down old stack)') {
      steps {
        script {
          if (isUnix()) {
            sh 'docker compose -f ${COMPOSE_FILE} down || true'
          } else {
            bat 'docker compose -f %COMPOSE_FILE% down || ver > nul'
          }
        }
      }
    }

    /* ------------------------------ */
    stage('Checkout Source Code') {
      steps {
        checkout scm
      }
    }

    /* ------------------------------ */
    stage('Build Docker Images') {
      steps {
        script {
          if (isUnix()) {
            sh 'docker compose -f ${COMPOSE_FILE} build --no-cache'
          } else {
            bat 'docker compose -f %COMPOSE_FILE% build --no-cache'
          }
        }
      }
    }

    /* ------------------------------ */
    stage('Push to Docker Hub') {
      steps {
        script {
          if (!isUnix()) {
            bat """
              docker login -u %DOCKER_USER% -p %DOCKER_PASS%
              docker tag ems-main-backend %DOCKER_USER%/cms-backend:latest
              docker tag ems-main-frontend %DOCKER_USER%/cms-frontend:latest
              docker push %DOCKER_USER%/cms-backend:latest
              docker push %DOCKER_USER%/cms-frontend:latest
            """
          }
        }
      }
    }

    /* ------------------------------ */
    stage('Deploy (Docker Compose Up)') {
      steps {
        script {
          if (isUnix()) {
            sh 'docker compose -f ${COMPOSE_FILE} up -d'
          } else {
            bat 'docker compose -f %COMPOSE_FILE% up -d'
          }
        }
      }
    }

    /* ------------------------------ */
    stage('Health Check') {
      steps {
        script {
          if (isUnix()) {
            sh 'echo "Skipping Linux health checks"'
          } else {
            bat 'powershell -Command "Start-Sleep -Seconds 5"'

            // Frontend Health
            bat 'powershell -Command "Try { Invoke-WebRequest http://localhost:%FRONTEND_PORT%/ -UseBasicParsing | Out-Null; Write-Host \'Frontend OK\'; } Catch { Write-Host \'Frontend warning\' }"'

            // Backend Health
            bat 'powershell -Command "Try { Invoke-WebRequest http://localhost:%BACKEND_PORT%/actuator/health -UseBasicParsing | Out-Null; Write-Host \'Backend OK\'; } Catch { Write-Host \'Backend warning\' }"'
          }
        }
      }
    }

  } // END stages

  /* ------------------------------ */
  post {
    success {
      echo "🎉 Deployment SUCCESS!"
      echo "Frontend → http://localhost:${env.FRONTEND_PORT}"
      echo "Backend  → http://localhost:${env.BACKEND_PORT}"
    }

    failure {
      echo '❌ Deployment failed. Showing container logs...'
      script {
        if (isUnix()) {
          sh 'docker ps -a'
          sh 'docker compose -f ${COMPOSE_FILE} logs --no-color --tail=200 || true'
        } else {
          bat 'docker ps -a'
          bat 'docker compose -f %COMPOSE_FILE% logs --no-color --tail=200'
        }
      }
    }
  }

}


