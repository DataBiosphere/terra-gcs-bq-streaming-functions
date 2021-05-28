# Terra Cloud Functions Repository

## Overview
This repository contains lightweight, portable functions that target the `Java 11` runtime for Google Cloud Functions.

The open source Java Functions Framework provides an API that can be used to author your functions, as well as an invoker which can be called to run the functions locally on your machine, or anywhere with a `Java 11` environment.

Cloud Build resources that enable remote builds to take place in the cloud can be customized in `cloudbuild.yaml`.

There are a number of Java Deployment options for deploying cloud functions. For portability purpose, we have chosen to package cloud functions and all dependencies as Uber JAR archives.

For more details, please refer to the following articles

https://cloud.google.com/functions/docs/first-java#gradle_1

https://cloud.google.com/functions/docs/concepts/java-deploy#gradle

Although there is only one cloud function (GcsBQ.java) at the moment. Both `build.gradle` and `cloudbuild.yaml` can easily be extended to build and package multiple Uber JARs for different cloud functions.

## Current CI/CD Pipeline

The current pipeline is to mirror this repository into Google Project Cloud Repository.

When a push request occurs, it triggers the steps defined in `cloudbuild.yaml` to build the cloud function artifacts (this will be the Uber JARs mentioned earlier).

The artifacts can be zipped and sent to Google Storage Bucket for cloud function deployment.

We can also use GitHub Action Workflows to build the artifacts and use `gcloud` command to deploy the cloud functions.

#### Under Development: terra-gcs-bq-streaming-functions

`GcsBQ` is our skeleton code for `GCS` to `BQ` Streaming. 