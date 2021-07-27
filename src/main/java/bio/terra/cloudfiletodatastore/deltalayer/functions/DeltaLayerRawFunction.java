package bio.terra.cloudfiletodatastore.deltalayer.functions;

import bio.terra.cloudfiletodatastore.FileCreatedMessageHarness;
import bio.terra.cloudfiletodatastore.FileMessage;
import bio.terra.cloudfiletodatastore.GsonConverter;
import bio.terra.cloudfiletodatastore.deltalayer.DeltaLayerMessageProcessor;
import bio.terra.cloudfunctions.common.GsonWrapper;
import com.google.cloud.functions.Context;
import com.google.cloud.functions.RawBackgroundFunction;
import com.google.events.cloud.storage.v1.StorageObjectData;
import java.util.logging.Logger;

public class DeltaLayerRawFunction
    implements RawBackgroundFunction, FileCreatedMessageHarness<StorageObjectData> {

  private static final Logger logger = Logger.getLogger(DeltaLayerRawFunction.class.getName());

  @Override
  public void accept(String s, Context context) throws Exception {
    logger.info(String.format("Here's the message %s", s));
    StorageObjectData storageObjectData = GsonConverter.convertFromClass(s, StorageObjectData.class);
    logger.info(String.format("Here's the serialized object %s", storageObjectData));
    new DeltaLayerMessageProcessor(convertMessage(storageObjectData)).processMessage();
  }

  @Override
  public FileMessage convertMessage(StorageObjectData toConvert) {
    return new FileMessage(toConvert.getName(), toConvert.getBucket(), toConvert.getSize(), null);
  }
}
