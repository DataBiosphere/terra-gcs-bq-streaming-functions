package bio.terra.cloudfunctions.common;

import com.google.cloud.functions.CloudEventsFunction;
import io.cloudevents.CloudEvent;

public abstract class CloudEventsHarness implements CloudEventsFunction {
  protected CloudEvent event;

  @Override
  public void accept(CloudEvent event) throws Exception {
    this.event = event;
  }

  public CloudEvent getEvent() {
    return event;
  }
}
