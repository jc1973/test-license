#!/usr/bin/env groovy

def call(body) {
  def config = [:]
  body.resolveStrategy = Closure.DELEGATE_FIRST
  body.delegate = config
  body()

  pipeline {
      agent any
      parameters {
          choice(name: 'Environment', choices:  config.environments , description: 'Choose environment')
          string(name: 'Version', defaultValue: '', description: 'Enter version to release, eg. "thl-cms-content-api-${version}.zip"')
      }
  
  
      options {
          timeout(time: 60 )
      }
  
      environment {
          AWS_DEFAULT_REGION="${config.region}"
          AWS_PROFILE="${config.aws_profile}"
          NPM_CONFIG_USERCONFIG='/var/lib/jenkins/.npmrc.nexus'
          // NEXUS_URL='https://nexus.odin.digital/repository/generic-releases/thl-cms-content-api/releases'
          ARTIFACT="${config.artifact}-${env.Version}.zip"
          // RDS_ZONE='cltmsmwctp09.eu-west-1.rds.amazonaws.com'
          TIME_STAMP = getTimeStamp()
          // NEXUS_CREDS = 'cfdbdb68-d82f-4818-9292-28881c4560db'
      }
  
  
      stages {
  
          stage('Download artifact from Nexus') {
            steps {
              cleanWs()
              withCredentials([usernamePassword(credentialsId: 'cfdbdb68-d82f-4818-9292-28881c4560db', passwordVariable: 'NEXUS_PASSWORD', usernameVariable: 'NEXUS_USER')]) {
                println 'Retrieving artifact'
/*
                sh '''
                  curl -s -X GET -u ${NEXUS_USER}:${NEXUS_PASSWORD} ${NEXUS_URL}/${ARTIFACT} -o ${ARTIFACT}
                '''
*/
              }
            }
          }
  
          stage('Unzip artifact') {
            steps {
              println 'Extracting artifact:' + env.ARTIFACT
/*
              sh '''
                unzip ${ARTIFACT}
              '''
*/
              println 'Unzipped: ' + env.ARTIFACT
            }
          }
  
          stage('Snapshot RDS instance') {
            environment {
              RDS_INSTANCE = getRDSInstanceForEnv(params.Environment)
              RDS_ENDPOINT = "${RDS_INSTANCE}.${env.RDS_ZONE}"
              AWS_REGION = "${AWS_DEFAULT_REGION}"
            }
            steps {
              sh '''
                echo would execute: aws rds create-db-snapshot --db-snapshot-identifier ${RDS_INSTANCE}-${TIME_STAMP} --db-instance-identifier ${RDS_INSTANCE}
                echo RDS_ENDPOINT is : $RDS_ENDPOINT
                # aws rds create-db-snapshot --db-snapshot-identifier ${RDS_INSTANCE}-${TIME_STAMP} --db-instance-identifier ${RDS_INSTANCE}
              '''
              println 'Waiting for snapshot to complete'
              timeout(10) {
  
                waitUntil {
                  sleep 10
                  script {
                  snapshotAvailable = sh (
                    script: '''
                        echo "would execute: aws rds describe-db-snapshots --db-snapshot-identifier ${RDS_INSTANCE}-${TIME_STAMP} | grep 'Status' | grep 'available'"
                        # aws rds describe-db-snapshots --db-snapshot-identifier ${RDS_INSTANCE}-${TIME_STAMP} | grep 'Status' | grep 'available'
                        ''', returnStatus: true
                    )
                    return (snapshotAvailable == 0)
                  }
                }
              }
              println 'Snapshot complete'
            }
          }
  
  /*
          stage('Database Migration') {
            environment {
              RDS_INSTANCE = getRDSInstanceForEnv(params.Environment)
              RDS_ENDPOINT = "${RDS_INSTANCE}.${env.RDS_ZONE}"
              AWS_REGION = env.AWS_DEFAULT_REGION
            }
            steps {
              nodejs(nodeJSInstallationName: 'NodeJS 8.15.0') {
              sh '''
                NODE_ENV=${Environment} SSH_USER=jenkins SSH_KEY_PATH=/var/lib/jenkins/.ssh/id_rsa npm run db:migrate
              '''
              }
            }
          }
  */ 
  
  /*
          stage('Deploy') { 
            environment {
              RDS_INSTANCE = getRDSInstanceForEnv(params.Environment)
              RDS_ENDPOINT = "${RDS_INSTANCE}.${env.RDS_ZONE}"
              AWS_REGION = env.AWS_DEFAULT_REGION
            }
            steps {
              nodejs(nodeJSInstallationName: 'NodeJS 8.15.0') {
                  sh '''
                    # NODE_ENV=${Environment} SSH_USER=jenkins SSH_KEY_PATH=/var/lib/jenkins/.ssh/id_rsa npm deploy
                    SLS_DEBUG=* NODE_ENV=${Environment} SSH_USER=jenkins SSH_KEY_PATH=/var/lib/jenkins/.ssh/id_rsa node_modules/.bin/serverless --stage ${Environment} deploy
                  '''
              }
            }
          }
  */
  
      }
  }
}



def getTimeStamp() {
  now = new Date().format("yyyy-MM-dd-HHmm", TimeZone.getTimeZone('UTC'))
  return now
}


def getRDSInstanceForEnv(environment) {
  environmentRDS = [ 'dev' : 'thl-dev',
                     'test' : 'thl-test',
                     'stage' : 'thl-stage',
                     'prod' : 'thl-production'
  ]
  return environmentRDS[environment]
}
