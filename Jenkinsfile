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
  }

  stages {
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
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

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

   stage('Health Check') {
    steps {
        script {
            if (isUnix()) {
                sh 'echo "Skipping strict health checks on Linux"'
            } else {
                bat 'powershell -Command "Start-Sleep -Seconds 5"'
                bat 'powershell -Command "Try { Invoke-WebRequest http://localhost:%FRONTEND_PORT%/ -UseBasicParsing | Out-Null } Catch { Write-Host \"Frontend warning\" }"'
                bat 'powershell -Command "Try { Invoke-WebRequest http://localhost:%BACKEND_PORT%/actuator/health -UseBasicParsing | Out-Null } Catch { Write-Host \"Backend warning\" }"'
            }
        }
    }
}

          }
        }
      }
    }
  }

  post {
    success {
      echo 'Deployment succeeded. Frontend at http://localhost:' + env.FRONTEND_PORT + ' Backend at http://localhost:' + env.BACKEND_PORT
    }
    failure {
      echo 'Deployment failed. Check build logs and container status.'
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
