package bio.terra.cloudfiletodatastore.deltalayer.functions;

import bio.terra.cloudfiletodatastore.FileCreatedMessageHarness;
import bio.terra.cloudfiletodatastore.FileUploadedMessage;
import bio.terra.cloudfiletodatastore.GsonConverter;
import bio.terra.cloudfiletodatastore.deltalayer.DeltaLayerFileUploadedMessageProcessor;
import com.google.cloud.functions.CloudEventsFunction;
import com.google.events.cloud.storage.v1.StorageObjectData;
import io.cloudevents.CloudEvent;
import java.util.Objects;

/**
 * GCF implementation using cloud events https://cloudevents.io/
 **/
public class DeltaLayerCloudFunction
    implements CloudEventsFunction, FileCreatedMessageHarness<StorageObjectData> {

  @Override
  public void accept(CloudEvent event) throws Exception {
    byte[] eventBytes = Objects.requireNonNull(event.getData()).toBytes();
    StorageObjectData storageObjectData =
        GsonConverter.convertFromClass(new String(eventBytes), StorageObjectData.class);
    new DeltaLayerFileUploadedMessageProcessor(convertMessage(storageObjectData)).processMessage();
  }

  @Override
  public FileUploadedMessage convertMessage(StorageObjectData toConvert) {
    return MessageConverter.getFileMessage(toConvert);
  }
}
