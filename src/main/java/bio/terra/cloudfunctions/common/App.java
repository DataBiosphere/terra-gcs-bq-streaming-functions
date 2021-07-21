package bio.terra.cloudfunctions.common;

import bio.terra.cloudevents.v1.CloudEventType;

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
  private CloudEventType cloudEventType;
  private Object message;

  public App(CloudEventType cloudEventType, Object message) {
    this.cloudEventType = cloudEventType;
    this.message = message;
  }

  public CloudEventType getCloudEventType() {
    return cloudEventType;
  }

  public Object getMessage() {
    return message;
  }

  public abstract void process() throws Exception;
}
