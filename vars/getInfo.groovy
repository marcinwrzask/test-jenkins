def call(Map config = [:]) {
  if (config['packageName'] == null) {
    error(['"packageName" argument is mandatory', help()].join('\n'))
  }
  if (config['credentialsID'] == null) {
    awsCredendialsID = 'aws-codeartifact'
  } else {
    awsCredendialsID = config['credentialsID']
  }

def packageVersionCall = {
  format = sh(returnStdout: true, script:
    """#!/bin/bash
    aws codeartifact list-packages \
    --region us-east-1 \
    --domain spanning \
    --repository shared \
    --output text \
    --query "packages[?package=='${config.packageName}'].format" """).trim()
  namespace = sh(returnStdout: true, script:
    """#!/bin/bash
    aws codeartifact list-packages \
    --region us-east-1 \
    --domain spanning \
    --repository shared \
    --output text \
    --query "packages[?package=='${config.packageName}'].namespace" """).trim()
  packageVersion = sh(returnStdout: true, script:
    """#!/bin/bash
    aws codeartifact list-package-versions \
    --region us-east-1 \
    --domain spanning \
    --repository shared \
    --package ${config.packageName} \
    --format ${format} \
    --namespace ${namespace} \
    --max-results 1 \
    --sort-by PUBLISHED_TIME \
    --output text \
    --query "versions[*].[version]" """).trim()
}

  if (config.useAwsInstanceProfile) {
    // access by credentials
    withCredentials([[
      $class: 'AmazonWebServicesCredentialsBinding',
      credentialsId: awsCredendialsID,
      accessKeyVariable: 'AWS_ACCESS_KEY_ID',
      secretKeyVariable: 'AWS_SECRET_ACCESS_KEY'
    ]]) {
      packageVersionCall()
    }
  } else {
    packageVersionCall()
  }
  echo "${packageVersion}"
  return packageVersion
}

def help() {
  '''
--------------------------------------------------
Help:
    codeArtifactGetLatestPackageVersion retrieves informations about latest version of the selected package from CodeArtifact repository.

    arguments:
      - packageName                        - package name which should be selected in pipeline script
      - credentialsID (optional)           - to run locally specify locall credentialsId (Dashboard->Credentials->System->Global credentials (unrestricted))
      - useAwsInstanceProfile (optional)   - to run locally specify useAwsInstanceProfile argument with value 'true' otherwise the script will run remotly 
    usage:
      codeArtifactGetLatestPackageVersion packageName: "metrics",

    required plugins:
      - CloudBees AWS Credentials
--------------------------------------------------
'''
}
