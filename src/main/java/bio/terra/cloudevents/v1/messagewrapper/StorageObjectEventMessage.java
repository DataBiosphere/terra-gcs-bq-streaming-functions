package bio.terra.cloudevents.v1.messagewrapper;

import com.google.events.cloud.storage.v1.StorageObjectData;

/** Storage object event message mapper. */
public final class StorageObjectEventMessage extends EventMessage<StorageObjectData> {
  public StorageObjectEventMessage(byte[] value, ToTarget<StorageObjectData> mapper) {
    super(value, mapper);
  }
}
