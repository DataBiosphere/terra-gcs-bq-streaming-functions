package bio.terra.cloudfiletodatastore.deltalayer.functions;

import bio.terra.cloudfiletodatastore.FileCreatedMessageHarness;
import bio.terra.cloudfiletodatastore.FileMessage;
import bio.terra.cloudfiletodatastore.GsonConverter;
import bio.terra.cloudfiletodatastore.deltalayer.DeltaLayerMessageProcessor;
import com.google.cloud.functions.CloudEventsFunction;
import com.google.events.cloud.storage.v1.StorageObjectData;
import io.cloudevents.CloudEvent;
import java.util.Objects;

public class DeltaLayerCloudFunction
    implements CloudEventsFunction, FileCreatedMessageHarness<StorageObjectData> {

  @Override
  public void accept(CloudEvent event) throws Exception {
    byte[] eventBytes = Objects.requireNonNull(event.getData()).toBytes();
    StorageObjectData storageObjectData =
        GsonConverter.convertFromClass(new String(eventBytes), StorageObjectData.class);
    new DeltaLayerMessageProcessor(convertMessage(storageObjectData)).processMessage();
  }

  @Override
  public FileMessage convertMessage(StorageObjectData toConvert) {
    return new FileMessage(toConvert.getName(), toConvert.getBucket(), toConvert.getSize(), null);
  }
}
