package bio.terra.cloudfunctions.proto;

import bio.terra.cloudfunctions.common.CloudEventsHarness;
import io.cloudevents.CloudEvent;
import java.util.logging.Logger;

/** This class is a subclass of CloudEventsHarness (i.e. a Cloud Function) with a custom App. */
public class ProtoFunc extends CloudEventsHarness {
  private static final Logger logger = Logger.getLogger(ProtoFunc.class.getName());

  // Can be injected through DI framework (Spring or Java CDI).
  private ProtoApp app;

  public ProtoFunc(ProtoApp app) {
    this.app = app;
  }

  @Override
  public void accept(CloudEvent event) throws Exception {
    super.accept(event);
    app = new ProtoApp(getCloudEventType(), getMessage());
    app.process();
  }
}
