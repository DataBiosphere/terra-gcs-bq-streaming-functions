package bio.terra.cloudfunctions.proto;

import bio.terra.cloudevents.CloudStorageEventType;
import bio.terra.cloudfunctions.common.AppReceiver;
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
    AppReceiver appReceiver = new AppReceiver();
    appReceiver.setCloudStorageEventType(CloudStorageEventType.fromCode(getContext().eventType()));
    appReceiver.setContentType(event.getContentType());
    appReceiver.setBucket(event.getBucket());
    appReceiver.setName(event.getName());
    ProtoApp app = new ProtoApp(appReceiver);
    app.process();
  }
}
