# Terra Cloud Functions Repository

## Overview
This repository contains lightweight, portable functions that target the `Java 11` runtime for Google Cloud Functions.

The open source Java Functions Framework provides an API that can be used to author your functions, as well as an invoker which can be called to run the functions locally on your machine, or anywhere with a `Java 11` environment.

Cloud Build resources that enable remote builds to take place in the cloud can be customized in `cloudbuild.yaml`.

There are a number of Java Deployment options for deploying cloud functions. For portability purpose, we have chosen to package cloud functions and all dependencies as Uber JAR archives.

For more details, please refer to the following articles

https://cloud.google.com/blog/topics/developers-practitioners/deploying-serverless-platforms-github-actions

https://cloud.google.com/functions/docs/first-java#gradle_1

https://cloud.google.com/functions/docs/concepts/java-deploy#gradle

This repository has the following cloud functions

| Java Class                                  | Cloud Function             | Application |
|---------------------------------------------|----------------------------|-------------|
| TestRunnerStreamingFunction.java            | gcs-bq-function            | Test Runner |
| ProtoFunc.java                              | generic-cloudevent-handler | For demo    |
|                                             |                            | Delta Layer |

#### Under Development: terra-gcs-bq-streaming-functions

`GcsBQ` is our skeleton code for `GCS` to `BQ` Streaming.

`ProtoFunc` illustrates a minimal cloud function by extending `CloudStorageEventHarness` and the use of the `getEvent(Class)` method to cast the underlying `LinkedTreeMap` to any event pojo.

## Deploying to serverless platforms with GitHub Actions

The name of the GitHub Actions workflows are
* `build-testrunner-functions.yml` for Test Runner (GcsBQ).
* `build-proto-cf-handler.yml` for ProtoFunc.

The Gradle task `shadowJar` builds the Uber JAR in the local default `build/libs` directory.
The UBER JAR contains the expected file structure for successful cloud functions deployment.

This workflow made certain assumptions about the cloud function runtime service account that users should be familiar with.
It also made certain custom parameters available to `java11` runtime as `envvar`'s that overcomes some limitations in the current Google Cloud Function `java11` binding.
Ultimately all custom values will come from `deltalayer` as `tfvar`'s so that the cloud function code can be built once and deployed as various cloud functions across different environments.
For more information on `deltalayer`, please refer to
* [terraform-ap-modules](https://github.com/broadinstitute/terraform-ap-modules/tree/master/deltalayer) and
* [terraform-ap-deployments](https://github.com/broadinstitute/terraform-ap-deployments/tree/master/deltalayer).

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| `GCLOUD_PROJECT` | Specify the Google Project ID that the Cloud Function deploys to. | `string` | n/a | yes |
| `service-account` | The runtime Service Account email assumed by Cloud Function | `string` | `PROJECT_ID@appspot.gserviceaccount.com` | no |
| `GOOGLE_APPLICATION_CREDENTIALS` | The Application Default Credentials that possess `cloudfunction.admin` role | `string` | n/a | yes |
| `NAME` | The ID of the function or fully qualified identifier for the function | `string` | n/a | yes |
| `trigger-resource` | The name of the google storage bucket (the string after gs://) that triggers cloud function for consumption. | `string` | n/a | yes |
| `trigger-event` | Specifies which action should trigger the function. For a list of acceptable values, call `gcloud functions event-types list`. | `string` | n/a | yes |
| `entry-point` | The fully qualified Java class name of the function. | `string` | n/a | yes |
| `set-env-vars` | List of comma-separated key-value pairs to set as environment variables. All existing environment variables will be removed first. | `string` | n/a | no |


