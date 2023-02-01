def call(Map config = [:]) {
     sh "aws codeartifact list-package-versions --region us-east-1 --domain spanning --repository shared --package ${config.package} --format maven --namespace com.github.SpanningCloudApps.stitch --max-results 1 --sort-by PUBLISHED_TIME "

}

