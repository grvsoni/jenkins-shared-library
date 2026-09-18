#!/usr/bin/env groovy

/**
 * runConfigJob
 *
 * Shared-library entrypoint used by every job. The parameters are read from
 * the job's `config.json` INSIDE the Jenkinsfile and passed in explicitly.
 *
 * Every job's config.json has the SAME two string keys but DIFFERENT values:
 *   - "serviceName"
 *   - "environment"
 *
 * Usage from a Jenkinsfile:
 *   @Library('shared-lib') _
 *
 *   node {
 *       checkout scm
 *       def config = readJSON file: 'config.json'
 *       runConfigJob(serviceName: config.serviceName, environment: config.environment)
 *   }
 *
 * @param args.serviceName  the service to act on (required)
 * @param args.environment  the target environment (required)
 */
def call(Map args = [:]) {
    String serviceName = args.serviceName
    String environment = args.environment

    if (!serviceName || !environment) {
        error "runConfigJob requires non-empty 'serviceName' and 'environment' params. Got: ${args}"
    }

    echo "=================================================="
    echo " Running shared-library function: runConfigJob"
    echo "   serviceName : ${serviceName}"
    echo "   environment : ${environment}"
    echo "=================================================="

    // ---- Do the actual (simple) work with the params ----
    stage("Deploy ${serviceName} to ${environment}") {
        echo "Deploying service '${serviceName}' into the '${environment}' environment..."
        echo "(this is a demo step showing the params from config.json are used)"
    }
}
