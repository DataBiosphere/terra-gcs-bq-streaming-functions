package bio.terra.cloudfunctions.streaming;

import bio.terra.cloudfunctions.common.CloudEventsHarness;
import io.cloudevents.CloudEvent;
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
    logger.info("EventDataBytes: " + new String(event.getData().toBytes()));
  }
}
