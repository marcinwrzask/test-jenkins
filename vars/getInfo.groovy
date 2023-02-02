def call(Map config = [:]) {
    def local = true
    def remote = false

    if (local) {
    // access by credentials
    withCredentials([[
    $class: 'AmazonWebServicesCredentialsBinding',
    credentialsId: 'aws-codeartifact',
    accessKeyVariable: 'AWS_ACCESS_KEY_ID',
    secretKeyVariable: 'AWS_SECRET_ACCESS_KEY']]) {
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
        --query "versions[*].[version]" """).trim() }
        echo "Latest packge version is: ${version}"
    }
}
