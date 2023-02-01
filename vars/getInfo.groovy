def call(Map config = [:]) {


// potrzebne zmienne w zależnosci od projektu
format = 
namespace = 



    withCredentials([[
    $class: 'AmazonWebServicesCredentialsBinding',
    credentialsId: 'aws-codeartifact',
    accessKeyVariable: 'AWS_ACCESS_KEY_ID',
    secretKeyVariable: 'AWS_SECRET_ACCESS_KEY']]) {
            script_version = []
            script_version = sh "aws codeartifact list-package-versions --region us-east-1 --domain spanning --repository shared --package ${config.package} --format maven --namespace com.github.SpanningCloudApps.stitch --max-results 1 --sort-by PUBLISHED_TIME "
            sh "echo script_version[0]['versions'][0]['version']"
        }
}

