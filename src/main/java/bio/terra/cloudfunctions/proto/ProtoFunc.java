package bio.terra.cloudfunctions.proto;

import bio.terra.cloudevents.GCSEvent;
import bio.terra.cloudfunctions.common.CloudStorageEventHarness;
import java.util.logging.Logger;

/**
 * This class is a subclass of CloudEventsHarness (i.e. a Cloud Function) with a custom App.
 *
 * <p>The no-arg and setter method is for supporting Cloud Function initialization and for this
 * function to be potentially used in a managed DI framework as a service.
 */
public class ProtoFunc extends CloudStorageEventHarness<GCSEvent> {
  private static final Logger logger = Logger.getLogger(ProtoFunc.class.getName());

  // Can be injected through DI framework (Spring or Java CDI).
  private ProtoApp app;

  public ProtoFunc() {}

  public ProtoFunc(ProtoApp app) {
    this.app = app;
  }

  public void setApp(ProtoApp app) {
    this.app = app;
  }

  @Override
  public void doAccept() throws Exception {
    // logger.info(getContext().eventType());
    // logger.info(getEvent().getBucket());
    // Implement business logic here.
  }
}
