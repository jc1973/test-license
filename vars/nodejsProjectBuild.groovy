#!/usr/bin/env groovy


def call(body) {
  def config = [:]
  body.resolveStrategy = Closure.DELEGATE_FIRST
  body.delegate = config
  body()
   
  pipeline {
    agent any
    options {
      timeout(time: config.timeout )
    }

  environment {
    ARTIFACT="${JOB_BASE_NAME}-${BUILD_NUMBER}.zip"
    NPM_CONFIG_USERCONFIG='/var/lib/jenkins/.npmrc.nexus'
    NEXUS_URL="${config.nexus_url}"
    NEXUS_CREDS="${config.nexus_creds}"
    // NEXUS_CREDS = credentials('cfdbdb68-d82f-4818-9292-28881c4560db')
    // GIT_COMMIT = sh(returnStdout: true, script: "git log -n 1 --pretty=format:'%h'").trim()
    // STAGE="dev"
    // REGION="eu-west-1"
  }
     
    stages {
       
      stage('Tag commit') {
        steps {
          withCredentials([usernamePassword(credentialsId: config.nexus_creds, passwordVariable: 'GIT_PASSWORD', usernameVariable: 'GIT_USERNAME')]) {
            sh '''
            #!/bin/bash
            # REPO=$(echo ${GIT_URL} | sed 's/https:..//g')
            # REPO=$(echo ${GIT_URL} | sed 's/https:..//g')
            env
            # echo git tag ${BRANCH_NAME}-${BUILD_NUMBER} ${GIT_COMMIT}
            # echo git tag ${BRANCH_NAME}-${BUILD_NUMBER} ${GIT_COMMIT}
            # echo git push https://${GIT_USERNAME}:${GIT_PASSWORD}@${REPO} --tags
            # echo git push https://${GIT_USERNAME}:${GIT_PASSWORD}@${REPO} --tags
            '''
          }
        }
      }

      stage('Build App') {
        steps {
          dir(config.directory) {
            // testing
            sh "ls"
            sh "pwd"
            // nodejs(nodeJSInstallationName: body.nodeJSVersion) {
              // sh "npm install --registry ${params.nexusHost}"
              // sh "npm install --registry ${params.nexusHost}"
              // sh "npm install --registry ${params.nexusHost}"
              // sh "npm install --registry ${params.nexusHost}"
            // }  
          }
        }
      }
       
      stage("Unit Tests") {
        steps {
          dir('client') {
            // testing
            sh "ls"
            sh "pwd"
            sh 'echo #### jenkins variables ###'
            sh 'printenv'
            sh 'echo ### Environment Variables:'
            sh 'env'
            // nodejs(nodeJSInstallationName: body.nodeJSVersion) {
              //   sh "npm test"
            // }
          }
        }
      }
       
       
       
    }
  }
}



