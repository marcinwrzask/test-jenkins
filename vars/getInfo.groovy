def call(Map config = [:]) {
    // potrzebne zmienne w zależnosci od projektu
    withCredentials([[
    $class: 'AmazonWebServicesCredentialsBinding',
    credentialsId: 'aws-codeartifact',
    accessKeyVariable: 'AWS_ACCESS_KEY_ID',
    secretKeyVariable: 'AWS_SECRET_ACCESS_KEY']]) {
        formate = sh(returnStdout: true, script: """#!/bin/bash
        aws codeartifact list-packages \
        --region us-east-1 \
        --domain spanning \
        --repository shared \
        --output text \
        --query "packages[?package=='${config.packageName}'].format" """ )}
        echo "${config.packageName}"
        echo "${formate}"
        println "${formate}"
        }
