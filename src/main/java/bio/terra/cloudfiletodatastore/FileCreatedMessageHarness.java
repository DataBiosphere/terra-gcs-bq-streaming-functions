package bio.terra.cloudfiletodatastore;

import bio.terra.cloudfiletodatastore.deltalayer.functions.DeltaLayerBackgroundFunction;

/**
 * Event listeners like {@link DeltaLayerBackgroundFunction} should implement this interface to
 * convert the message to {@link FileUploadedMessage}
 *
 * @param <T> the cloud provider message type like GcsEvent
 */
public interface FileCreatedMessageHarness<T> {

  FileUploadedMessage convertMessage(T toConvert);
}
