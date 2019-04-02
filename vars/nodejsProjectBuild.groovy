#!/usr/bin/env groovy

// TODO: change this into a re-usable global pipeline library
/* Steps already found (listed in order)
   4 build
   4 SonarQube analysis
   3 Unit Tests
   3 Build App
   2 test
   2 Clean workspace
   1 test jenkins file
   1 deploy-uat
   1 deploy-test
   1 deploy-prod
*/

def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
// def call(Map pipelineParams) {

    pipeline {
        agent any
        options {
            //timeout(time: 10 )
            timeout(time: body.timeout )
    
        }
/* No enviroment in shared Global Library    
        environment {
            // Using iam roles
            // AWS_PROFILE=""
            // AWS_DEFAULT_REGION="eu-west-1"
            // AWS_PROFILE="nands-crm"
            // NPM_CONFIG_USERCONFIG='/var/lib/jenkins/.npmrc.nexus'
            // Need to check if creds are necessary
            // NEXUS_URL='https://nexus.odin.digital/repository/generic-releases/deltatre/releases'
            // NEXUS_HOST='https://nexus.odin.digital'
            // NEXUS_REPO='/repository/generic-releases/generic-releases/nands/unity/client'
            // NEXUS_CREDS = credentials('cfdbdb68-d82f-4818-9292-28881c4560db')
        }
*/
    
        stages {
    
            stage("Clean workspace") {
              steps {
                cleanWs() - deletes sonar properties file
              }
            }
    
            stage('Build App') {
                steps {
    /*
                    slackSend channel: '#pei_jenkins',
                    color: 'warning',
                    message: "Started ${env.JOB_NAME} ${env.BUILD_NUMBER} (<${env.BUILD_URL}|Open>)"
    
    */

                    dir(body.directory) {
                        // testing
                        sh "ls"
                        sh "pwd"
                        // nodejs(nodeJSInstallationName: body.nodeJSVersion) {
                        // sh "npm install --registry ${params.nexusHost}"
                    // }
                }
            }
    
            stage("Unit Tests") {
                steps {
                    dir('client') {
                        // testing
                        sh "ls"
                        sh "pwd"
                        // nodejs(nodeJSInstallationName: body.nodeJSVersion) {
                        //   sh "npm test"
                        // }
                    }
                }
            }
   
/* 
            stage ("SonarQube analysis") {
                steps {
                   dir('client') {
                        script {
                          def scannerHome = tool 'SonarScanner';
                          withSonarQubeEnv('SonarCloud') {
                            nodejs(nodeJSInstallationName: body.nodeJSVersion) {
                              sh "${scannerHome}/bin/sonar-scanner"
                            }
                          }
                        }
                    }
                }
            }
*/


        }
    }
}
