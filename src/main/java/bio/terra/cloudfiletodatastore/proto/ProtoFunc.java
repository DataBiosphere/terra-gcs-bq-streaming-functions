package bio.terra.cloudfiletodatastore.proto;

import bio.terra.cloudevents.CloudStorageEventType;
import bio.terra.cloudfiletodatastore.FileUploadedMessage;
import bio.terra.cloudfunctions.common.GoogleCloudStorageEventHarness;
import com.google.events.cloud.storage.v1.StorageObjectData;

/**
 * This class is a subclass of CloudEventsHarness (i.e. a Cloud Function) with a custom App.
 *
 * <p>The no-arg and setter method is for supporting Cloud Function initialization and for this
 * function to be potentially used in a managed DI framework as a service.
 */
public class ProtoFunc extends GoogleCloudStorageEventHarness {

  @Override
  public void doAccept() throws Exception {
    StorageObjectData event = getEvent(StorageObjectData.class);
    // App can be injected through DI framework (Spring or Java CDI).
    FileUploadedMessage fileUploadedMessage =
        new FileUploadedMessage(
            CloudStorageEventType.fromCode(getContext().eventType()),
            event.getContentType(),
            event.getName(),
            event.getBucket(),
            event.getSize(),
            event.getTimeCreated());
    ProtoApp app = new ProtoApp(fileUploadedMessage);
    app.process();
  }
}
