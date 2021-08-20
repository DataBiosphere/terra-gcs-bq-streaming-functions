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
            "Malformed event data: Expected {0} event from bucket {1} of content type {2} but received {3} event from bucket {4} of content type {5}.",
            new Object[] {
              GoogleCloudEventHarness.GOOGLE_STORAGE_OBJECT_FINALIZE,
              expectedBucket,
              "application/gzip",
              getContext().eventType(),
              event.getBucket(),
              event.getContentType()
            });
        return;
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE, "An unexpected error occurred.", e);
      throw new RuntimeException(e);
    }
  }
}
