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
    NODE_VERSION="NodeJS ${config.node_version}"
    NPM_REGISTRY_SERVER="${config.nexus_registry}"
    DIRECTORY=checkNullDirectory(config.directory)
  }
     
    stages {
       
      stage('Tag commit') {
        steps {
          withCredentials([usernamePassword(credentialsId: config.nexus_creds, passwordVariable: 'GIT_PASSWORD', usernameVariable: 'GIT_USERNAME')]) {
            sh '''
            #!/bin/bash
            # REPO=$(echo ${GIT_URL} | sed 's/https:..//g')
            # REPO=$(echo ${GIT_URL} | sed 's/https:..//g')
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
          dir(env.DIRECTORY) {
            /*
            slackSend channel: '#thl_jenkins',
            color: 'warning',
            message: "Started ${env.JOB_NAME} ${env.BUILD_NUMBER} (<${env.BUILD_URL}|Open>)"
            */
            sh "pwd"
            cleanWs()
            // nodejs(nodeJSInstallationName: env.NODE_VERSION) {
            //   sh '''
            //   npm install --registry ${NPM_REGISTRY_SERVER}
            //   '''
            // }
          }
        }
      }

       
      stage("Unit Tests") {
        steps {
          dir(env.DIRECTORY) {
            // testing
            sh "ls"
            sh "pwd"
            sh 'printenv'
            sh 'env'
            // nodejs(nodeJSInstallationName: env.NODE_VERSION) {
            //   sh "npm test"
            // }
          }
        }
      }

  
       
       
       
    }
  }
}



def checkNullDirectory(directory) {
  if  (directory) {
  } else {
  directory = '.'
  }
  return directory
}  
  
}
