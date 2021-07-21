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
import java.util.logging.Logger;

/**
 * This class is an abstract representation of a Cloud Function and contains only framework-specific
 * logic to receive and parse CloudEvent messages.
 *
 * <p>Sub-classes of this class can be integrated with a DI framework to deploy Cloud Functions like
 * a Service.
 */
public abstract class CloudEventsHarness implements CloudEventsFunction {
  private static final Logger logger = Logger.getLogger(CloudEventsHarness.class.getName());

  private CloudEvent event;
  private Object message;

  /**
   * Subclasses of this class must override and invoke this method at the minimum.
   *
   * @param event
   * @throws Exception
   */
  @Override
  public void accept(CloudEvent event) throws Exception {
    this.event = event;
    parse();
  }

  public CloudEvent getEvent() {
    return event;
  }

  public Object getMessage() {
    return message;
  }

  public CloudEventType getCloudEventType() {
    return CloudEventType.fromCode(event.getType());
  }

  /**
   * The parse() method delegates message parsing to specific implementation that can be replaced in
   * the fture.
   *
   * @throws Exception
   */
  public void parse() throws Exception {
    parseCloudEventV1();
  }

  private void parseCloudEventV1() throws Exception {
    CloudEventType eventType = getCloudEventType();
    switch (eventType) {
      case FIRESTORE_DOCUMENT_V1_CREATED:
      case FIRESTORE_DOCUMENT_V1_DELETED:
      case FIRESTORE_DOCUMENT_V1_UPDATED:
      case FIRESTORE_DOCUMENT_V1_WRITTEN:
        message =
            new FirestoreEventMessage(
                event.getData().toBytes(),
                d -> GsonWrapper.getInstance().fromJson(new String(d), DocumentEventData.class));
        break;
      case PUBSUB_TOPIC_V1_MESSAGE_PUBLISHED:
        message =
            new PubSubEventMessage(
                event.getData().toBytes(),
                d -> GsonWrapper.getInstance().fromJson(new String(d), MessagePublishedData.class));
        break;
      case STORAGE_OBJECT_V1_ARCHIVED:
      case STORAGE_OBJECT_V1_DELETED:
      case STORAGE_OBJECT_V1_FINALIZED:
      case STORAGE_OBJECT_V1_METADATAUPDATED:
        message =
            new StorageObjectEventMessage(
                event.getData().toBytes(),
                d -> GsonWrapper.getInstance().fromJson(new String(d), StorageObjectData.class));
        logger.info(
            String.format("Event implementation class: %s", message.getClass().getTypeName()));
        break;
      default:
        logger.warning(String.format("Unsupported event type '%s'.", event.getType()));
        break;
    }
  }
}
