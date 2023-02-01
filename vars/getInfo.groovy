def call(Map config = [:]) {
    // potrzebne zmienne w zależnosci od projektu
    withCredentials([[
    $class: 'AmazonWebServicesCredentialsBinding',
    credentialsId: 'aws-codeartifact',
    accessKeyVariable: 'AWS_ACCESS_KEY_ID',
    secretKeyVariable: 'AWS_SECRET_ACCESS_KEY']]) {
        listRepo = readJSON(text: sh(returnStdout: true,
        script: "aws codeartifact list-package-versions \
        --region us-east-1 \
        --domain spanning \
        --repository shared \
        --package ${config.package} \
        --format maven \
        --namespace com.github.SpanningCloudApps.stitch \
        --max-results 1 \
        --sort-by PUBLISHED_TIME").trim())}
        println "The follow json obj is ${listRepo}" 
        }

