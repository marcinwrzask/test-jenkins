def call(Map config = [:]) {
withCredentials([[
    $class: 'AmazonWebServicesCredentialsBinding',
    credentialsId: 'aws',
    accessKeyVariable: 'AWS_ACCESS_KEY_ID',
    secretKeyVariable: 'AWS_SECRET_ACCESS_KEY']]) {
        sh "aws codeartifact list-package-versions --domain spanning --repository shared --package ${config.package} --format maven --namespace com.github.SpanningCloudApps.stitch --max-results 1 --sort-by PUBLISHED_TIME "
    }

}

