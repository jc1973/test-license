#!/usr/bin/env groovy


def getBuildUser {
  userId = sh (
    returnStdout: true,
    script: '''
      grep "userId" ${JENKINS_HOME}/jobs/${JOB_NAME}/builds/${BUILD_NUMBER}/build.xml | head -1 | cut -d ">" -f 2 | cut -d "<" -f 1
    '''
  ).trim()
  return userId
}
  
