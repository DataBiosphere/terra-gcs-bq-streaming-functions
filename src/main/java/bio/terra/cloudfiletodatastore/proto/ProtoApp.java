package bio.terra.cloudfiletodatastore.proto;

import bio.terra.cloudevents.CloudStorageEventType;
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
    if (CloudStorageEventType.GOOGLE_STORAGE_OBJECT_FINALIZE
        .getCode()
        .equals(fileUploadedMessage.getCloudStorageEventType().getCode())) {
      logger.info(
          String.format(
              "Received %s event from bucket %s of content type %s, name %s",
              fileUploadedMessage.getCloudStorageEventType().getDesc(),
              fileUploadedMessage.getSourceBucket(),
              fileUploadedMessage.getContentType(),
              fileUploadedMessage.getResourceName()));
    }
  }
}
