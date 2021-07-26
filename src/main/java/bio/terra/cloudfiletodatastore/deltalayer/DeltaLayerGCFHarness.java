package bio.terra.cloudfiletodatastore.deltalayer;

import bio.terra.cloudevents.GCSEvent;
import bio.terra.cloudfiletodatastore.FileMessage;
import bio.terra.cloudfiletodatastore.FileCreatedMessageHarness;
import com.google.cloud.functions.BackgroundFunction;
import com.google.cloud.functions.Context;
import java.util.logging.Logger;

/**
 * Implementation of a GCF background function
 * https://cloud.google.com/functions/docs/writing#background_functions
 * This and other background functions or other message platform listeners should
 * pull the info needed out of the message and delegate to a {@link bio.terra.cloudfiletodatastore.MessageProcessor}
 * subclass.
 */
public class DeltaLayerGCFHarness
    implements BackgroundFunction<GCSEvent>, FileCreatedMessageHarness<GCSEvent> {

  private static final Logger logger = Logger.getLogger(DeltaLayerGCFHarness.class.getName());

  @Override
  public void accept(GCSEvent gcsEvent, Context context) throws Exception {
    //TODO: verify bucket?, check file size, check file type, return a proper error response
    logger.info(String.format("We received this event as storage object data %s", gcsEvent));
    new DeltaLayerMessageProcessor(convertMessage(gcsEvent)).processMessage();
  }

  @Override
  public FileMessage convertMessage(GCSEvent toConvert) {
    return new FileMessage(
        toConvert.getName(),
        toConvert.getBucket(),
        toConvert.getSize(),
        toConvert.getTimeCreated());
  }
}
