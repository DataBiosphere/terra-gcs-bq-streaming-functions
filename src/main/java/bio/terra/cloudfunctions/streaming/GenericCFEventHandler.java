package bio.terra.cloudfunctions.streaming;

import bio.terra.cloudfunctions.common.CloudEventsHarness;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.v1.CloudEventV1;
import java.util.logging.Logger;

public class GenericCFEventHandler extends CloudEventsHarness {
  private static final Logger logger = Logger.getLogger(GenericCFEventHandler.class.getName());

  @Override
  public void accept(CloudEvent event) throws Exception {
    super.accept(event);
    logger.info("Type: " + event.getType());
    logger.info("DataContentType: " + event.getDataContentType());
    logger.info("Source: " + event.getSource().toString());
    logger.info("TypeName: " + event.getClass().getTypeName());
    logger.info("Test 1: " + CloudEventV1.class.isInstance(event));
    logger.info("Test 2: " + (event.getClass().getTypeName().equals(CloudEventV1.class.getTypeName())));
    logger.info("Test 3: " + CloudEventV1.class);
    logger.info("Data Schema: " + event.getDataSchema());
    if (CloudEventV1.class.isInstance(event)) {
      if (event.getType().equals("google.cloud.storage.object.v1.finalized")) {
        logger.info("Data TypeName: " + event.getData().getClass().getTypeName());
        logger.info("Data Schema: " + event.getDataSchema().toString());
      }
    }
    logger.info("EventDataBytes: " + new String(event.getData().toBytes()));
  }
}
