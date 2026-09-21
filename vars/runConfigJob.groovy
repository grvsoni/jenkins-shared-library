#!/usr/bin/env groovy

/**
 * runConfigJob
 *
 * Shared-library entrypoint used by every job. The parameters are read from
 * the job's `config.json` INSIDE the Jenkinsfile and passed in explicitly.
 *
 * Expected config.json shape (same keys in every job, different values):
 *   {
 *     "serviceName": "payments-api",
 *     "environment": "dev",
 *     "deploy":        { "replicas": 1, "region": "us-east-1", "strategy": "rolling" },
 *     "notifications": { "slack": "#deploys-dev", "email": ["team@example.com"] }
 *   }
 *
 * Usage from a Jenkinsfile:
 *   @Library('shared-lib') _
 *
 *   node {
 *       checkout scm
 *       def config = readJSON file: 'config.json'
 *       runConfigJob(
 *           serviceName:   config.serviceName,
 *           environment:   config.environment,
 *           deploy:        config.deploy,
 *           notifications: config.notifications
 *       )
 *   }
 *
 * @param args.serviceName    the service to act on (required)
 * @param args.environment    the target environment (required)
 * @param args.deploy         map: replicas, region, strategy (optional)
 * @param args.notifications  map: slack, email[] (optional)
 */
def call(Map args = [:]) {
    String serviceName = args.serviceName
    String environment = args.environment
    Map deploy = (args.deploy ?: [:]) as Map
    Map notifications = (args.notifications ?: [:]) as Map

    if (!serviceName || !environment) {
        error "runConfigJob requires non-empty 'serviceName' and 'environment' params. Got: ${args}"
    }

    // Deploy settings with sensible fallbacks so the job still runs if a key is missing.
    def replicas = deploy.replicas ?: 1
    String region = deploy.region ?: 'us-east-1'
    String strategy = deploy.strategy ?: 'rolling'

    // Notification settings.
    String slack = notifications.slack ?: '(none)'
    List emails = (notifications.email ?: []) as List

    echo "=================================================="
    echo " Running shared-library function: runConfigJob"
    echo "   serviceName : ${serviceName}"
    echo "   environment : ${environment}"
    echo "   replicas    : ${replicas}"
    echo "   region      : ${region}"
    echo "   strategy    : ${strategy}"
    echo "=================================================="

    // ---- Do the actual (simple) work with the params ----
    stage("Deploy ${serviceName} to ${environment}") {
        echo "Deploying service '${serviceName}' into the '${environment}' environment..."
        echo "Rolling out ${replicas} replica(s) to '${region}' using the '${strategy}' strategy."
        echo "(this is a demo step showing the params from config.json are used)"
    }

    stage('Notify') {
        echo "Notifying Slack channel: ${slack}"
        if (emails) {
            echo "Emailing: ${emails.join(', ')}"
        } else {
            echo "No email recipients configured."
        }
    }
}
