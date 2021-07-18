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
import org.openjdk.jol.vm.VM;

public abstract class CloudEventsHarness implements CloudEventsFunction {
  private static final Logger logger = Logger.getLogger(CloudEventsHarness.class.getName());

  protected Class<? extends CloudEvent> realization;
  protected CloudEvent event;
  protected String messageTypeName;
  protected Object message;

  @Override
  public void accept(CloudEvent event) throws Exception {
    this.event = event;
    this.realization = this.event.getClass();
  }

  public CloudEvent getEvent() {
    return event;
  }

  public Object getMessage() {
    return message;
  }

  public <T> T getMessage(Class<T> clazz) {
    return clazz.cast(message);
  }

  public boolean isCloudEventV1() {
    Class<CloudEventV1> cloudEventV1ClassObj = CloudEventV1.class;
    logger.info("isCloudEventV1: " + cloudEventV1ClassObj);
    logger.info("isCloudEventV1: " + realization);
    logger.info("isCloudEventV1: " + VM.current().addressOf(cloudEventV1ClassObj));
    logger.info("isCloudEventV1: " + VM.current().addressOf(realization));
    logger.info("hashCode: " + cloudEventV1ClassObj.hashCode());
    logger.info("hashCode: " + realization.hashCode());
    logger.info("isCloudEventV1: " + CloudEventV1.class.equals(realization));
    logger.info("isCloudEventV1: " + CloudEventV03.class.equals(realization));
    logger.info("isCloudEventV1: " + event.getClass().getTypeName());
    logger.info("isCloudEventV1: " + event.getClass().getName());
    logger.info("isCloudEventV1: " + event.getClass().getCanonicalName());
    logger.info("isCloudEventV1: " + event.getClass().getSimpleName());
    logger.info("isCloudEventV1: " + CloudEvent.class.isInstance(event));
    logger.info("isCloudEventV1: " + CloudEventV1.class.isInstance(event));
    logger.info(
        "isCloudEventV1 assignable: " + CloudEvent.class.isAssignableFrom(event.getClass()));
    logger.info(
        "isCloudEventV1 assignable: " + CloudEventV1.class.isAssignableFrom(event.getClass()));
    try {
      logger.info(
          "isCloudEventV1 assignable: "
              + event.getClass().getDeclaredConstructor().getParameterCount());
    } catch (NoSuchMethodException e) {
      logger.info(e.getMessage());
    }
    // .newInstance(null, null, null, null, null, null, null, null, null)));

    return CloudEventV1.class.isInstance(event);
    // return CloudEventV1.class.getTypeName().equals(event.getClass().getTypeName());
  }

  public boolean isCloudEventV03() {
    logger.info("isCloudEventV03: " + event.getClass().getTypeName());
    logger.info("isCloudEventV03: " + CloudEventV03.class.isInstance(event));
    logger.info("isCloudEventV03: " + event.getClass().isAssignableFrom(CloudEventV03.class));
    return CloudEventV03.class.isInstance(event);
    // return CloudEventV03.class.getTypeName().equals(event.getClass().getTypeName());
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
        messageTypeName = FirestoreEventMessage.class.getTypeName();
        break;
      case PUBSUB_TOPIC_V1_MESSAGE_PUBLISHED:
        message =
            new PubSubEventMessage(
                event.getData().toBytes(),
                d -> GsonWrapper.getInstance().fromJson(new String(d), MessagePublishedData.class));
        messageTypeName = PubSubEventMessage.class.getTypeName();
        break;
      case STORAGE_OBJECT_V1_ARCHIVED:
      case STORAGE_OBJECT_V1_DELETED:
      case STORAGE_OBJECT_V1_FINALIZED:
      case STORAGE_OBJECT_V1_METADATAUPDATED:
        message =
            new StorageObjectEventMessage(
                event.getData().toBytes(),
                d -> GsonWrapper.getInstance().fromJson(new String(d), StorageObjectData.class));
        StorageObjectEventMessage m = StorageObjectEventMessage.class.cast(message);
        messageTypeName = StorageObjectEventMessage.class.getTypeName();
        logger.info(
            "getGenericSuperclass: "
                + StorageObjectEventMessage.class.getGenericSuperclass().getTypeName());
        // logger.info(
        //    "Concrete event data: "
        //        + Class.forName(messageTypeName).asSubclass().cast(message).getMessage());
        break;
      default:
        break;
    }
  }

  private void parseCloudEventV03() {
    throw new UnsupportedOperationException("parseCloudEventV03 method not yet supported");
  }
}
