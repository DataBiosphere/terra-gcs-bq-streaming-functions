package bio.terra.cloudfiletodatastore.deltalayer.functions;

import static bio.terra.cloudfiletodatastore.deltalayer.functions.MessageConverter.getFileMessage;

import bio.terra.cloudfiletodatastore.FileUploadedMessage;
import bio.terra.cloudfiletodatastore.deltalayer.DeltaLayerBQSQLWriter;
import bio.terra.cloudfiletodatastore.deltalayer.DeltaLayerFileUploadedMessageProcessor;
import bio.terra.cloudfiletodatastore.deltalayer.GcsFileFetcher;
import bio.terra.cloudfunctions.common.GsonWrapper;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.functions.Context;
import com.google.cloud.functions.RawBackgroundFunction;
import com.google.events.cloud.storage.v1.StorageObjectData;
import java.util.logging.Logger;

/**
 * Very similar to {@link DeltaLayerBackgroundFunction} but here we implement a
 * RawBackgroundFunction
 * https://javadoc.io/static/com.google.cloud.functions/functions-framework-api/1.0.1/com/google/cloud/functions/RawBackgroundFunction.html
 * the event payload is passed as a String and we can deserialize using a Gson instance that knows
 * how to deserialize to {@link java.time.OffsetDateTime} and therefore we can use Google's {@link
 * StorageObjectData}
 */
public class DeltaLayerRawFunction implements RawBackgroundFunction {

  private static final Logger logger = Logger.getLogger(DeltaLayerRawFunction.class.getName());

  @Override
  public void accept(String s, Context context) throws Exception {
    logger.info(String.format("Here's the message %s", s));
    StorageObjectData storageObjectData = GsonWrapper.convertFromClass(s, StorageObjectData.class);
    logger.info(String.format("Here's the serialized object %s", storageObjectData));
    FileUploadedMessage message = getFileMessage(storageObjectData);
    new DeltaLayerFileUploadedMessageProcessor(
            message,
            new DeltaLayerBQSQLWriter(),
            BigQueryOptions.getDefaultInstance().getService(),
            new GcsFileFetcher(message.getSourceBucket(), message.getResourceName()))
        .processMessage();
  }
}
