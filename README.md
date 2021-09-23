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

This repository contains the following Java sources for various Cloud Function applications

| Java Class                                  | Cloud Function              | Application |
|---------------------------------------------|-----------------------------|-------------|
| TestRunnerStreamingFunction.java            | testrunner-results-streamer | TestRunner results streamer function |
| DeltaLayerRawFunction.java                  |                             | Delta Layer |

## Deploying Cloud Functions to Java 11 Runtime with GitHub Actions

The `deploy-cloud-function` action deploys a Cloud Function to the appropriate environment, it accepts the following input parameters

| Input                        | Description                | Required    | Default |
|------------------------------|----------------------------|-------------|---------|
| deployer-sa-email            | The email address of the IAM service account used for deploying the cloud function                                    | Yes | n/a |
| deployer-sa-key              | The IAM service account key used for deploying the cloud function                                                     | Yes | n/a |
| service-account              | The email address of the IAM service account associated with the function at runtime                                  | Yes | n/a |
| func                         | The fully specified name of the cloud function                                                                        | Yes | n/a |
| entry-point                  | Name of a Google Cloud Function (as defined in source code) that will be executed                                     | Yes | n/a |
| memory                       | Limit on the amount of memory the function can use (128MB, 256MB, 512MB, 1024MB, 2048MB, 4096MB, and 8192MB)          | No  | 256MB |
| trigger-bucket               | Google Cloud Storage bucket name. Every change in files in this bucket will trigger function execution                | Yes | n/a |
| project                      | GCP Project                                                                                                           | Yes | n/a |
| region                       | The Cloud region for the function. Overrides the default functions/region property value for this command invocation  | No  | us-central1 |
| source                       | Local directory of cloud function source                                                                              | No  | . |
| env-vars-file                | Path to a local YAML file with definitions for all environment variables. All existing environment variables will be removed before the new environment variables are added | No | n/a |                                                                                                 | Yes |

Please refer to `deploy-testrunner-dev-streamer.yml` for a working example.

The `deploy-cloud-function` action requires a deployer service account (identified as `deployer-sa-key`, `deployer-sa-email`) for cloud function deployment and an identity (`service-account`) that the cloud function assumes at runtime.
These service accounts are provisioned by Terraform and their secrets are stored in the vault.

For more information, please refer to
* [deltalayer module](https://github.com/broadinstitute/terraform-ap-modules/tree/master/deltalayer)
* [deltalayer deployments](https://github.com/broadinstitute/terraform-ap-deployments/tree/master/deltalayer)
* [testrunner module](https://github.com/broadinstitute/terraform-ap-modules/tree/master/testrunner)
* [testrunner deployments](https://github.com/broadinstitute/terraform-ap-deployments/tree/master/testrunner)

## Moving Vault secrets to Github via Atlantis

For the purpose of Cloud Function deployment using the `deploy-cloud-function` action. 
The required vault secrets need to be moved to this Github repo via Atlantis, the process is documented [here](https://docs.google.com/document/d/1JbjV4xjAlSOuZY-2bInatl4av3M-y_LmHQkLYyISYns/edit#heading=h.kor6m5ppv2u).

For a working example of moving the secrets tto this repo, please see

[github/tfvars/databiosphere-terra-gcs-bq-streaming-functions.tfvars](https://github.com/broadinstitute/terraform-ap-deployments/blob/master/github/tfvars/databiosphere-terra-gcs-bq-streaming-functions.tfvars)

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

The following workflow assumes that the Github secrets (e.g. DEV_PROTO_CF_DEPLOYER_SA, DEV_PROTO_FUNC_SA) already exists in the repo.

```build-proto-cf-handler.yml```
```shell
# This workflow will build a backend cloud function with Gradle
# For more information see: https://github.com/google-github-actions/deploy-cloud-functions

name: Build and deploy ProtoFunc cloud function

on:
  push:
    branches: [ main ]

jobs:
  deploy-protofunc:

    runs-on: ubuntu-latest
    env:
      DEPLOY_ENV: DEV
    steps:
      - uses: actions/checkout@v2

      - name: Configure secrets, SA, Project ID
        id: config-step
        run: |
          DEPLOYER_SA_KEY=$(echo -n ${{ secrets[format('{0}_PROTO_CF_DEPLOYER_SA', env.DEPLOY_ENV)] }} | base64 -d)
          DEPLOYER_SA_EMAIL=$(echo -n $DEPLOYER_SA_KEY | jq -r .client_email)
          PROJECT_ID=$(echo -n $DEPLOYER_SA_KEY | jq -r .project_id)
          FUNC_SA_KEY=$(echo -n ${{ secrets[format('{0}_PROTO_FUNC_SA', env.DEPLOY_ENV)] }} | base64 -d)
          FUNC_SA_EMAIL=$(echo -n $FUNC_SA_KEY | jq -r .client_email)
          echo ::add-mask::$DEPLOYER_SA_KEY
          echo ::add-mask::$DEPLOYER_SA_EMAIL
          echo ::add-mask::$FUNC_SA_KEY
          echo ::add-mask::$FUNC_SA_EMAIL
          echo ::set-output name=deployer-sa-email::$DEPLOYER_SA_EMAIL
          echo ::set-output name=deployer-sa-key::$DEPLOYER_SA_KEY
          echo ::set-output name=project-id::$PROJECT_ID
          echo ::set-output name=func-sa-email::$FUNC_SA_EMAIL
          echo ::set-output name=project-id::$PROJECT_ID

      # Deploy cloud function from the source
      - name: Deploy the ProtoFunc function
        id: deploy-proto-cloudfunc
        uses: ./.github/actions/deploy-cloud-function
        with:
          func: proto-cloudfunc
          trigger-bucket: ${{ steps.config-step.outputs.project-id }}-proto-bucket
          entry-point: bio.terra.cloudfiletodatastore.proto.ProtoFunc
          memory: 512MB
          project: ${{ steps.config-step.outputs.project-id }}
          service-account: ${{ steps.config-step.outputs.func-sa-email }}
          deployer-sa-email: ${{ steps.config-step.outputs.deployer-sa-email }}
          deployer-sa-key: ${{ steps.config-step.outputs.deployer-sa-key }}

```
