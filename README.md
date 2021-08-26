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
| DeltaLayerRawFunction.java                  |                            | Delta Layer |

## Deploying Cloud Functions to serverless platforms with GitHub Actions

The name of the GitHub Actions workflows are
* `build-testrunner-functions.yml` for Test Runner (`TestRunnerStreamingFunction`).
* `???` for Delta Layer.

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

## Tutorial

#### A Sample Cloud Function

```ProtoApp.java```
```java
package bio.terra.cloudfiletodatastore.proto;

import bio.terra.cloudfiletodatastore.FileUploadedMessage;
import bio.terra.cloudfiletodatastore.MessageProcessor;;
import java.util.logging.Logger;

/** This class is a subclass of App with a custom implementation of the process() logic. */
public class ProtoApp extends MessageProcessor {
  private static final Logger logger = Logger.getLogger(ProtoApp.class.getName());

  public ProtoApp(FileUploadedMessage fileUploadedMessage) {
    super(fileUploadedMessage);
  }

  // Business logic
  @Override
  public void processMessage() {
    String sourceBucket = message.getSourceBucket();
    String resourceName = message.getResourceName();
    String projectId = System.getenv("GCLOUD_PROJECT");
    String dataSet = System.getenv("BQ_DATASET");
    String table = System.getenv("BQ_TABLE");
    logger.info(
        String.format(
            "Received GCS event from GCP %s, source bucket %s, resource name %s for BigQuery %s.%s.",
            projectId, sourceBucket, resourceName, dataSet, table));
  }
}
```

```ProtoFunc.java```
```java
package bio.terra.cloudfiletodatastore.proto;

import bio.terra.cloudfiletodatastore.FileUploadedMessage;
import bio.terra.cloudfunctions.common.GoogleCloudEventHarness;
import bio.terra.cloudfunctions.common.MediaTypeWrapper;
import com.google.events.cloud.storage.v1.StorageObjectData;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is a subclass of CloudEventsHarness (i.e. a Cloud Function) with a custom App.
 *
 * <p>The no-arg and setter method is for supporting Cloud Function initialization and for this
 * function to be potentially used in a managed DI framework as a service.
 */
public class ProtoFunc extends GoogleCloudEventHarness {
    private static final Logger logger = Logger.getLogger(ProtoFunc.class.getName());

    @Override
    public void doAccept() {
        try {
            String expectedBucket = System.getenv("GOOGLE_BUCKET");
            StorageObjectData event = getEvent(StorageObjectData.class);
            MediaTypeWrapper mediaType = new MediaTypeWrapper(event.getContentType());
            if (isGoogleStorageObjectFinalize()
                    && expectedBucket.equals(event.getBucket())
                    && mediaType.isApplicationGzip()) {
                // App can be injected through DI framework (Spring or Java CDI).
                FileUploadedMessage fileUploadedMessage =
                        new FileUploadedMessage(
                                event.getName(), event.getBucket(), event.getSize(), event.getTimeCreated());
                ProtoApp app = new ProtoApp(fileUploadedMessage);
                app.process();
            } else {
                logger.log(
                        Level.SEVERE,
                        String.format(
                                "Malformed event data: Expected %s event from bucket %s of content type %s but received %s event from bucket %s of content type %s",
                                GoogleCloudEventHarness.GOOGLE_STORAGE_OBJECT_FINALIZE,
                                expectedBucket,
                                "application/gzip",
                                getContext().eventType(),
                                event.getBucket(),
                                event.getContentType()));
                return;
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "An unexpected error occurred.", e);
            throw new RuntimeException(e);
        }
    }
}
```

#### A Sample GitHub Action workflow for deploying Cloud Function

```build-proto-cf-handler.yml```
```shell
# This workflow will build a backend cloud function with Gradle
# For more information see: https://github.com/google-github-actions/deploy-cloud-functions

name: Build and deploy ProtoFunc cloud function

on:
  push:
    branches: [ main ]

jobs:
  build-and-deploy:

    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v2

      - name: Set up AdoptOpenJDK 11
        uses: joschi/setup-jdk@v2
        with:
          java-version: 11

      - name: Cache Gradle packages
        uses: actions/cache@v2
        with:
          path: |
            ~/.gradle/caches
            ~/.gradle/wrapper
          key: v1-${{ runner.os }}-gradle-${{ hashfiles('**/gradle-wrapper.properties') }}-${{ hashFiles('**/*.gradle') }}
          restore-keys: v1-${{ runner.os }}-gradle-${{ hashfiles('**/gradle-wrapper.properties') }}

      - name: Grant execute permission for gradlew
        run: chmod +x gradlew

      # Set up ADC for credentials
      # Ideally this step should be part of action.yml but
      # GHA does not yet support action within action, so we're stuck.
      # Please refer to https://github.com/actions/runner/pull/612 for updates.
      - uses: google-github-actions/setup-gcloud@master
        with:
          service_account_email: ${{ secrets.DEPLOY_CF_SA_EMAIL }}
          service_account_key: ${{ secrets.DEPLOY_CF_SA_KEY_JSON }}
          export_default_credentials: true

      - name: Build and deploy the ProtoFunc backend function
        id: build-and-deploy
        uses: ./.github/actions/build-and-deploy-backend-function
        with:
          func: generic-cloudevent-handler
          module: testrunner
          entry_point: bio.terra.cloudfiletodatastore.proto.ProtoFunc
          runtime_memory: 512MB
          bucket: ${{ env.GCLOUD_PROJECT }}-testrunner-results
          trigger: google.storage.object.finalize
          dataset: simple_stream_dataset

```