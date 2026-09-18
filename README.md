# jenkins-shared-library

A minimal Jenkins [Shared Library](https://www.jenkins.io/doc/book/pipeline/shared-libraries/)
consumed by three separate pipeline jobs.

## What it does

It exposes a single global step, `runConfigJob`, defined in
[`vars/runConfigJob.groovy`](vars/runConfigJob.groovy). The step:

1. Receives two parameters: `serviceName` and `environment`.
2. Uses them to run a (demo) deploy stage.

The parameters are read from each job's `config.json` **inside the Jenkinsfile**
and passed in explicitly. Every consuming job ships the **same two keys** in its
`config.json` but with **different values**, so the shared function behaves
differently per job while the code stays identical.

## Repository layout

```
vars/
  runConfigJob.groovy   # global var -> callable as runConfigJob()
```

> Shared libraries must follow this exact directory convention. Global steps
> live in `vars/`, the file name is the step name.

## Register it in Jenkins

**Manage Jenkins -> System -> Global Pipeline Libraries -> Add**

| Field                     | Value                                                  |
|---------------------------|--------------------------------------------------------|
| Name                      | `shared-lib`                                            |
| Default version           | `main`                                                  |
| Retrieval method          | Modern SCM -> Git                                       |
| Project Repository        | `https://github.com/grvsoni/jenkins-shared-library.git`|
| Load implicitly           | off (jobs load it explicitly via `@Library`)           |

Jobs then reference it with:

```groovy
@Library('shared-lib') _

node {
    checkout scm
    def config = readJSON file: 'config.json'
    runConfigJob(serviceName: config.serviceName, environment: config.environment)
}
```

## Plugin requirement

`readJSON` comes from the **Pipeline Utility Steps** plugin. Install it via
*Manage Jenkins -> Plugins* before running the jobs.
# jenkins-shared-library
