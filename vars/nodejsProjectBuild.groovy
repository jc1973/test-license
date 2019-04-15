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
  }
     
    stages {
       
      stage("Clean workspace") {
        steps {
          cleanWs() 
          println config.nexus_url
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
