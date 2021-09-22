package bio.terra.cloudfiletodatastore.testrunner.cloudfunctions;

import bio.terra.cloudfiletodatastore.FileUploadedMessage;
import bio.terra.cloudfunctions.common.GoogleCloudEventHarness;
import bio.terra.cloudfunctions.common.MediaTypeWrapper;
import com.google.events.cloud.storage.v1.StorageObjectData;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestRunnerStreamingFunction extends GoogleCloudEventHarness {
  private static final Logger logger =
      Logger.getLogger(TestRunnerStreamingFunction.class.getName());

  protected String expectedBucket;

  @Override
  public void doAccept() {
    try {
      loadEnvVars();
      StorageObjectData event = getEvent(StorageObjectData.class);
      MediaTypeWrapper mediaType = new MediaTypeWrapper(event.getContentType());
      if (!expectedBucket.equals(event.getBucket()) || !mediaType.isApplicationGzip()) {
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
      FileUploadedMessage fileUploadedMessage =
          new FileUploadedMessage(
              event.getName(), event.getBucket(), event.getSize(), event.getTimeCreated());
      processMessage(fileUploadedMessage);
    } catch (Exception e) {
      logger.log(Level.SEVERE, "An unexpected error occurred.", e);
      throw new RuntimeException(e);
    }
  }

  public void loadEnvVars() {
    expectedBucket = System.getenv("GOOGLE_BUCKET");
  }

  public void processMessage(FileUploadedMessage fileUploadedMessage) {
    TestRunnerStreamingProcessor processor = new TestRunnerStreamingProcessor(fileUploadedMessage);
    processor.processMessage();
  }
}
