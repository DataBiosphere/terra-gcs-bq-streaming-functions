package bio.terra.cloudfunctions.proto;

import bio.terra.cloudevents.CloudStorageEventType;
import bio.terra.cloudevents.GCSEvent;
import bio.terra.cloudfunctions.common.GoogleCloudStorageEventHarness;

/**
 * This class is a subclass of CloudEventsHarness (i.e. a Cloud Function) with a custom App.
 *
 * <p>The no-arg and setter method is for supporting Cloud Function initialization and for this
 * function to be potentially used in a managed DI framework as a service.
 */
public class ProtoFunc extends GoogleCloudStorageEventHarness {

  @Override
  public void doAccept() throws Exception {
    // App can be injected through DI framework (Spring or Java CDI).
    ProtoApp app =
        new ProtoApp(
            CloudStorageEventType.fromCode(getContext().eventType()), getEvent(GCSEvent.class));
    app.process();
  }
}
