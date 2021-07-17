package bio.terra.cloudfunctions.common;

import bio.terra.cloudevents.v1.CloudEventType;
import bio.terra.cloudevents.v1.messagewrapper.FirestoreEventMessage;
import bio.terra.cloudevents.v1.messagewrapper.PubSubEventMessage;
import bio.terra.cloudevents.v1.messagewrapper.StorageObjectEventMessage;
import com.google.cloud.functions.CloudEventsFunction;
import com.google.events.cloud.firestore.v1.DocumentEventData;
import com.google.events.cloud.pubsub.v1.MessagePublishedData;
import com.google.events.cloud.storage.v1.StorageObjectData;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.v03.CloudEventV03;
import io.cloudevents.core.v1.CloudEventV1;
import java.util.logging.Logger;

public abstract class CloudEventsHarness implements CloudEventsFunction {
  private static final Logger logger = Logger.getLogger(CloudEventsHarness.class.getName());

  protected CloudEvent event;
  protected Class<?> messageType;
  protected Object message;

  @Override
  public void accept(CloudEvent event) throws Exception {
    this.event = event;
    parse();
  }

  public CloudEvent getEvent() {
    return event;
  }

  public Class<?> getMessageType() {
    return messageType;
  }

  public Object getMessage() {
    return message;
  }

  public <T> T getMessage(Class<T> clazz) {
    return clazz.cast(message);
  }

  public boolean isCloudEventV1() {
    return CloudEventV1.class.getTypeName().equals(event.getClass().getTypeName());
  }

  public boolean isCloudEventV03() {
    return CloudEventV03.class.getTypeName().equals(event.getClass().getTypeName());
  }

  public void parse() throws Exception {
    if (isCloudEventV1()) {
      parseCloudEventV1();
    } else if (isCloudEventV03()) {
      parseCloudEventV03();
    }
  }

  private void parseCloudEventV1() throws Exception {
    CloudEventType eventType = CloudEventType.fromCode(event.getType());
    switch (eventType) {
      case FIRESTORE_DOCUMENT_V1_CREATED:
      case FIRESTORE_DOCUMENT_V1_DELETED:
      case FIRESTORE_DOCUMENT_V1_UPDATED:
      case FIRESTORE_DOCUMENT_V1_WRITTEN:
        message =
            new FirestoreEventMessage(
                event.getData().toBytes(),
                d -> GsonWrapper.getInstance().fromJson(new String(d), DocumentEventData.class));
        messageType = FirestoreEventMessage.class;
        break;
      case PUBSUB_TOPIC_V1_MESSAGE_PUBLISHED:
        message =
            new PubSubEventMessage(
                event.getData().toBytes(),
                d -> GsonWrapper.getInstance().fromJson(new String(d), MessagePublishedData.class));
        messageType = PubSubEventMessage.class;
        break;
      case STORAGE_OBJECT_V1_ARCHIVED:
      case STORAGE_OBJECT_V1_DELETED:
      case STORAGE_OBJECT_V1_FINALIZED:
      case STORAGE_OBJECT_V1_METADATAUPDATED:
        message =
            new StorageObjectEventMessage(
                event.getData().toBytes(),
                d -> GsonWrapper.getInstance().fromJson(new String(d), StorageObjectData.class));
        messageType = StorageObjectEventMessage.class;
        logger.info("Concrete event data: " + messageType.cast(message));
        break;
      default:
        break;
    }
  }

  private void parseCloudEventV03() {
    throw new UnsupportedOperationException("parseCloudEventV03 method not yet supported");
  }
}
