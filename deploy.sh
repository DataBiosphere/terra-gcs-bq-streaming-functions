#!/usr/bin/env bash
set -eo pipefail
set -x

VAULT_TOKEN="$1"
GIT_BRANCH="$2"
DEPLOY_ENV="$3"

set +x

if [[ -z "${DEPLOY_ENV}" ]]; then
    echo "DEPLOY_ENV argument not supplied; inferring from GIT_BRANCH '${GIT_BRANCH}'."

    #TODO
    if [[ "${GIT_BRANCH}" == "main" ]]; then
        DEPLOY_ENV="dev"
    elif [[ "${GIT_BRANCH}" == "alpha" ]]; then
        DEPLOY_ENV="alpha"
    elif [[ "${GIT_BRANCH}" == "perf" ]]; then
        DEPLOY_ENV="perf"
    elif [[ "${GIT_BRANCH}" == "staging" ]]; then
        DEPLOY_ENV="staging"
    else
        echo "Git branch '${GIT_BRANCH}' is not configured to automatically deploy to an environment"
        exit 1
    fi
fi

set -u

APP_PATH=/app
SERVICE_ACCT_KEY_FILE="deploy_account.json"
GCLOUD_IMAGE="google/cloud-sdk:latest"
DEPLOY_PROJECT_NAME="broad-dsde-${DEPLOY_ENV}"

if [[ "${DEPLOY_ENV}" =~ ^(dev|alpha|perf|staging)$ ]]; then
    SOURCE_ENV="${DEPLOY_ENV}"
else
    echo "Unknown environment: ${DEPLOY_ENV} - must be one of [dev, alpha, perf, staging]"
    exit 1
fi

echo "Deploying branch '${GIT_BRANCH}' from ${SOURCE_ENV} to ${DEPLOY_PROJECT_NAME}"
set -x

# Get the tier specific credentials for the service account out of Vault
# Put key into SERVICE_ACCT_KEY_FILE
docker run \
    --rm \
    --env "VAULT_TOKEN=${VAULT_TOKEN}" \
    broadinstitute/dsde-toolbox \
    vault read \
    --format=json "secret/dsde/firecloud/${DEPLOY_ENV}/deltalayer/sa/deltalayer-dev-deployer.json" |
jq .data > "${SERVICE_ACCT_KEY_FILE}"

docker run \
    --rm \
    --entrypoint="/bin/bash" \
    --volume "$PWD:${APP_PATH}" \
    --env BASE_URL="https://us-central1-deltalayer-${DEPLOY_ENV}.cloudfunctions.net" \
    "${GCLOUD_IMAGE}" \
    -c \
    "gcloud auth activate-service-account --key-file ${APP_PATH}/${SERVICE_ACCT_KEY_FILE} &&
      echo ${DEPLOY_PROJECT_NAME} &&
      gcloud config set project ${DEPLOY_PROJECT_NAME} &&
      gcloud auth list &&
      cd ${APP_PATH} &&
      gcloud functions deploy delta-layer-listener --trigger-bucket=terra-deltalayer-source-${SOURCE_ENV} \\
        --entry-point=bio.terra.cloudfiletodatastore.deltalayer.functions.DeltaLayerRawFunction \\
        --memory 512MB --source=. --runtime java11 --project ${DEPLOY_PROJECT_NAME} \\
        --service-account deltalayer-${SOURCE_ENV}-streamer@broad-dsde-${SOURCE_ENV}.iam.gserviceaccount.com"