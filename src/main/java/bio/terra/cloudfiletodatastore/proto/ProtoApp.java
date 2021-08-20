package bio.terra.cloudfiletodatastore.proto;

import bio.terra.cloudfiletodatastore.FileUploadedMessage;
import bio.terra.cloudfunctions.common.App;
import java.util.logging.Logger;

/** This class is a subclass of App with a custom implementation of the process() logic. */
public class ProtoApp extends App {
  private static final Logger logger = Logger.getLogger(ProtoApp.class.getName());

  public ProtoApp(FileUploadedMessage fileUploadedMessage) {
    super(fileUploadedMessage);
  }

  // Business logic
  @Override
  public void process() throws Exception {
    String sourceBucket = fileUploadedMessage.getSourceBucket();
    String resourceName = fileUploadedMessage.getResourceName();
    String projectId = System.getenv("GCLOUD_PROJECT");
    String dataSet = System.getenv("BQ_DATASET");
    String table = System.getenv("BQ_TABLE");
    logger.info(
        String.format(
            "Received GCS event from GCP %s, source bucket %s, resource name %s for BigQuery %s.%s.",
            projectId, sourceBucket, resourceName, dataSet, table));
  }
}
