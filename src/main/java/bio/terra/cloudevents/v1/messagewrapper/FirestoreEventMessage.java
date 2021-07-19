package bio.terra.cloudevents.v1.messagewrapper;

import com.google.events.cloud.firestore.v1.DocumentEventData;

public final class FirestoreEventMessage extends EventMessage<DocumentEventData> {
  public FirestoreEventMessage(byte[] value, ToTarget<DocumentEventData> mapper) {
    super(value, mapper);
  }
}
