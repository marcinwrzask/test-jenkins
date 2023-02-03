def call(Map config = [:]) {
  if (config['packageName'] == null) {
  error(['"packageName" argument is mandatory', help()].join("\n"))
  }

  def local = true
  def remote =false
  
  if (local) {
    // access by credentials
    withCredentials([
      $class: 'AmazonWebServicesCredentialsBinding',
      credentialsId: 'aws-codeartifact',
      accessKeyVariable: 'AWS_ACCESS_KEY_ID',
      secretKeyVariable: 'AWS_SECRET_ACCESS_KEY']) packageVersionCall } 

  if (remote) { packageVersionCall } 
  }

def packageVersionCall = '{
  format = sh(returnStdout: true, script: """#!/bin/bash
  aws codeartifact list-packages \
  --region us-east-1 \
  --domain spanning \
  --repository shared \
  --output text \
  --query "packages[?package=='${packageName}'].format" """).trim()

  namespace = sh(returnStdout: true, script: """#!/bin/bash
  aws codeartifact list-packages \
  --region us-east-1 \
  --domain spanning \
  --repository shared \
  --output text \
  --query "packages[?package=='${packageName}'].namespace" """).trim()

  sh(returnStdout: true, script: """#!/bin/bash
  aws codeartifact list-package-versions \
  --region us-east-1 \
  --domain spanning \
  --repository shared \
  --package ${packageName} \
  --format ${format} \
  --namespace ${namespace} \
  --max-results 1 \
  --sort-by PUBLISHED_TIME \
  --output text \
  --query "versions[*].[version]" """).trim()
}'


def help() {
'''
--------------------------------------------------
Help:
  codeArtifactGetLatestDependencies is shared library provide.

  arguments:
    - packageName                        - package name which should be selected in pipeline script

  usage:
    gitDiffBetweenCommits firstCommit: "jg3sj2lab",
                          secondCommit: "123sjdal"
--------------------------------------------------
'''
}
