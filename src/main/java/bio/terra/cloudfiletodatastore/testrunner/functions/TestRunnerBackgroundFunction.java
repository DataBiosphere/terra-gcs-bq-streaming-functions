package bio.terra.cloudfiletodatastore.testrunner.functions;

import bio.terra.cloudevents.CloudStorageEventType;
import bio.terra.cloudfiletodatastore.FileUploadedMessage;
import bio.terra.cloudfunctions.common.GoogleCloudStorageEventHarness;
import com.google.events.cloud.storage.v1.StorageObjectData;

public class TestRunnerBackgroundFunction extends GoogleCloudStorageEventHarness {
  @Override
  public void doAccept() throws Exception {
    StorageObjectData event = getEvent(StorageObjectData.class);
    FileUploadedMessage fileUploadedMessage =
        new FileUploadedMessage(
            CloudStorageEventType.fromCode(getContext().eventType()),
            event.getContentType(),
            event.getName(),
            event.getBucket(),
            event.getSize(),
            event.getTimeCreated());
    TestRunnerApp app = new TestRunnerApp(fileUploadedMessage);
    app.process();
  }
}
