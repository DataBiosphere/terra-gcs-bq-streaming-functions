package bio.terra.cloudfiletodatastore.deltalayer.functions;

import bio.terra.cloudevents.GCSEvent;
import bio.terra.cloudfiletodatastore.FileCreatedMessageHarness;
import bio.terra.cloudfiletodatastore.FileUploadedMessage;
import bio.terra.cloudfiletodatastore.deltalayer.DeltaLayerFileUploadedMessageProcessor;
import com.google.cloud.functions.BackgroundFunction;
import com.google.cloud.functions.Context;
import java.time.ZoneOffset;
import java.util.logging.Logger;

/**
 * Implementation of a GCF background function
 * https://cloud.google.com/functions/docs/writing#background_functions This would be the ideal if
 * not for the crummy Gson support which prevents us currently from using a Google maintained class
 * to capture the event data.
 */
public class DeltaLayerBackgroundFunction
    implements BackgroundFunction<GCSEvent>, FileCreatedMessageHarness<GCSEvent> {

  private static final Logger logger =
      Logger.getLogger(DeltaLayerBackgroundFunction.class.getName());

  @Override
  public void accept(GCSEvent gcsEvent, Context context) {
    logger.info(String.format("We received this event as storage object data %s", gcsEvent));
    new DeltaLayerFileUploadedMessageProcessor(convertMessage(gcsEvent)).processMessage();
  }

  @Override
  public FileUploadedMessage convertMessage(GCSEvent toConvert) {
    return new FileUploadedMessage(
        toConvert.getName(),
        toConvert.getBucket(),
        toConvert.getSize(),
        toConvert.getTimeCreated().toInstant().atOffset(ZoneOffset.UTC),
        toConvert.getContentType());
  }
}
