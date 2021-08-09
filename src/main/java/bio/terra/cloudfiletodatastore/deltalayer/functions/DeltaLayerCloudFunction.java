package bio.terra.cloudfiletodatastore.deltalayer.functions;

import static bio.terra.cloudfiletodatastore.deltalayer.functions.MessageConverter.getFileMessage;

import bio.terra.cloudfiletodatastore.FileUploadedMessage;
import bio.terra.cloudfiletodatastore.deltalayer.DeltaLayerBQJSONWriter;
import bio.terra.cloudfiletodatastore.deltalayer.DeltaLayerFileUploadedMessageProcessor;
import bio.terra.cloudfiletodatastore.deltalayer.GcsFileFetcher;
import bio.terra.cloudfunctions.common.GsonWrapper;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.functions.CloudEventsFunction;
import com.google.events.cloud.storage.v1.StorageObjectData;
import io.cloudevents.CloudEvent;
import java.util.Objects;

/** GCF implementation using cloud events https://cloudevents.io/ */
public class DeltaLayerCloudFunction implements CloudEventsFunction {

  @Override
  public void accept(CloudEvent event) throws Exception {
    byte[] eventBytes = Objects.requireNonNull(event.getData()).toBytes();
    StorageObjectData storageObjectData =
        GsonWrapper.convertFromClass(new String(eventBytes), StorageObjectData.class);
    FileUploadedMessage message = getFileMessage(storageObjectData);
    new DeltaLayerFileUploadedMessageProcessor(
            message,
            new DeltaLayerBQJSONWriter(),
            BigQueryOptions.getDefaultInstance().getService(),
            new GcsFileFetcher(message.getSourceBucket(), message.getResourceName()))
        .processMessage();
  }
}
