package bio.terra.cloudfunctions.proto;

import bio.terra.cloudevents.CloudStorageEventType;
import bio.terra.cloudfunctions.common.App;
import bio.terra.cloudfunctions.common.AppReceiver;
import java.util.logging.Logger;

/** This class is a subclass of App with a custom implementation of the process() logic. */
public class ProtoApp extends App {
  private static final Logger logger = Logger.getLogger(ProtoApp.class.getName());

  public ProtoApp(AppReceiver appReceiver) {
    super(appReceiver);
  }

  // Business logic
  @Override
  public void process() throws Exception {
    if (CloudStorageEventType.GOOGLE_STORAGE_OBJECT_FINALIZE
        .getCode()
        .equals(appReceiver.getCloudStorageEventType().getCode())) {
      logger.info(
          String.format(
              "Received %s event from bucket %s of content type %s, name %s",
              appReceiver.getCloudStorageEventType().getDesc(),
              appReceiver.getBucket(),
              appReceiver.getContentType(),
              appReceiver.getName()));
    }
  }
}
