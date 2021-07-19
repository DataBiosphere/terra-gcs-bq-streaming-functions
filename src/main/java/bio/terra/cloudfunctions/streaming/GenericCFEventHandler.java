package bio.terra.cloudfunctions.streaming;

import bio.terra.cloudevents.v1.CloudEventType;
import bio.terra.cloudevents.v1.messagewrapper.StorageObjectEventMessage;
import bio.terra.cloudfunctions.common.App;
import bio.terra.cloudfunctions.common.CloudEventsHarness;
import com.google.events.cloud.storage.v1.StorageObjectData;
import io.cloudevents.CloudEvent;
import java.util.logging.Logger;

public class GenericCFEventHandler extends CloudEventsHarness {
  private static final Logger logger = Logger.getLogger(GenericCFEventHandler.class.getName());
  private GenericApp app;

  @Override
  public void accept(CloudEvent event) throws Exception {
    super.accept(event);
    app = new GenericApp(getCloudEventType(), getMessage());
    app.process();
  }

  class GenericApp extends App {
    public GenericApp(CloudEventType eventType, Object message) {
      super(eventType, message);
    }

    @Override
    public void process() throws Exception {
      if (CloudEventType.STORAGE_OBJECT_V1_FINALIZED.getCode().equals(eventType.getCode())) {
        StorageObjectData data = ((StorageObjectEventMessage) getMessage()).getMessage();
        logger.info(
            String.format(
                "Received %s event from bucket %s of content type %s",
                getEventType().getDesc(), data.getBucket(), data.getContentType()));
      }
    }
  }
}
