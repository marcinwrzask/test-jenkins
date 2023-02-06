def call(Map config = [:]) {
  if (config['packageName'] == null) {
    error(['"packageName" argument is mandatory', help()].join("\n"))
  }
  if (config['credentialsID'] == null) {
    config['credentialsID'] = 'aws-codeartifact'
  }
  if (config['local'] == null) {
    remote = true
  }
  if (config['local'] == 'true') {
    remote = false
  }

  def packageVersionCall = {
    format = sh(returnStdout: true, script: """#!/bin/bash
    aws codeartifact list-packages \
    --region us-east-1 \
    --domain spanning \
    --repository shared \
    --output text \
    --query "packages[?package=='${config.packageName}'].format" """).trim()

    namespace = sh(returnStdout: true, script: """#!/bin/bash
    aws codeartifact list-packages \
    --region us-east-1 \
    --domain spanning \
    --repository shared \
    --output text \
    --query "packages[?package=='${config.packageName}'].namespace" """).trim()

    version = sh(returnStdout: true, script: """#!/bin/bash
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

  if (config.local) {
    // access by credentials
    withCredentials([[
      $class: 'AmazonWebServicesCredentialsBinding',
      credentialsId: config.credentialsID,
      accessKeyVariable: 'AWS_ACCESS_KEY_ID',
      secretKeyVariable: 'AWS_SECRET_ACCESS_KEY']]) { packageVersionCall() } 
    return version
  } 

  if (remote) {
    packageVersionCall() 
  } 
  println "${version}"
  return version
}

def help() {
'''
--------------------------------------------------
Help:
    codeArtifactGetLatestDependencies is shared library provide.

    arguments:
      - packageName                        - package name which should be selected in pipeline script
      - credentialsID (optional)           - to run locally specify locall credentialsId (Dashboard->Credentials->System->Global credentials (unrestricted))
      - local (optional)                   - to run locally specify local argument with value 'true'
    usage:
      codeArtifactGetLatestDependencies packageName: "metrics",

    required plugins:
      - CloudBees AWS Credentials
--------------------------------------------------
'''
}
