package bio.terra.cloudfiletodatastore;

/**
 * Event listeners like {@link bio.terra.cloudfiletodatastore.deltalayer.DeltaLayerGCFHarness}
 * should implement this interface to convert the message to {@link FileMessage}
 * @param <T> the cloud provider message type like GcsEvent
 */
public interface FileCreatedMessageHarness<T> {

  FileMessage convertMessage(T toConvert);

}
