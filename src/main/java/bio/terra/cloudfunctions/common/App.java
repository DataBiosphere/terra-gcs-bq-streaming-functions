package bio.terra.cloudfunctions.common;

import bio.terra.cloudevents.v1.CloudEventType;

public abstract class App {
  protected CloudEventType eventType;
  protected Object message;

  public App(CloudEventType eventType, Object message) {
    this.eventType = eventType;
    this.message = message;
  }

  public CloudEventType getEventType() {
    return eventType;
  }

  public Object getMessage() {
    return message;
  }

  public void process() throws Exception {
    throw new UnsupportedOperationException("process method must be overridden by sub-classes");
  }
}
