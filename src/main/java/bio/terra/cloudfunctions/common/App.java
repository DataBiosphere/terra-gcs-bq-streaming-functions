package bio.terra.cloudfunctions.common;

import bio.terra.cloudevents.CloudStorageEventType;
import bio.terra.cloudevents.GCSEvent;

/**
 * This abstract class represents the business application that interprets CloudEvent messages.
 *
 * <p>Sub-classes overrides the process() method with business logic to achieve desired functional
 * goals.
 *
 * <p>Sub-classes can be integrated with a DI framework to deploy the business logic like as a
 * Service.
 */
public abstract class App {
  private CloudStorageEventType cloudStorageEventType;
  private GCSEvent cloudStorageEvent;

  public App(CloudStorageEventType cloudStorageEventType, GCSEvent cloudStorageEvent) {
    this.cloudStorageEventType = cloudStorageEventType;
    this.cloudStorageEvent = cloudStorageEvent;
  }

  public CloudStorageEventType getCloudStorageEventType() {
    return cloudStorageEventType;
  }

  public GCSEvent getCloudStorageEvent() {
    return cloudStorageEvent;
  }

  public abstract void process() throws Exception;
}
