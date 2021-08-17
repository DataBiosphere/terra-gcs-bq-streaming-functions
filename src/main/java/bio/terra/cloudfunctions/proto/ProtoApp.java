package bio.terra.cloudfunctions.proto;

import bio.terra.cloudevents.CloudStorageEventType;
import bio.terra.cloudevents.GCSEvent;
import bio.terra.cloudfunctions.common.App;
import java.util.logging.Logger;

/** This class is a subclass of App with a custom implementation of the process() logic. */
public class ProtoApp extends App {
  private static final Logger logger = Logger.getLogger(ProtoApp.class.getName());

  public ProtoApp(CloudStorageEventType cloudStorageEventType, GCSEvent cloudStorageEvent) {
    super(cloudStorageEventType, cloudStorageEvent);
  }

  // Business logic
  @Override
  public void process() throws Exception {
    if (CloudStorageEventType.GOOGLE_STORAGE_OBJECT_FINALIZE
        .getCode()
        .equals(getCloudStorageEventType().getCode())) {
      logger.info(
          String.format(
              "Received %s event from bucket %s of content type %s",
              getCloudStorageEventType().getDesc(),
              getCloudStorageEvent().getBucket(),
              getCloudStorageEvent().getContentType()));
    }
  }
}
