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
     
    stages {
       
      stage("Clean workspace") {
        steps {
          cleanWs() 
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
            sh 'jenkins variables'
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
