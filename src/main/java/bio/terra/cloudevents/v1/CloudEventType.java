package bio.terra.cloudevents.v1;

/**
 * For a enumeration of all Cloud Event types, please refer to
 * https://github.com/googleapis/google-cloudevents/tree/master/jsonschema/google/events/cloud
 */
public enum CloudEventType {
  FIRESTORE_DOCUMENT_V1_CREATED(
      "google.cloud.firestore.document.v1.created", "Firestore Document Created (v1)"),
  FIRESTORE_DOCUMENT_V1_DELETED(
      "google.cloud.firestore.document.v1.deleted", "Firestore Document Deleted (v1)"),
  FIRESTORE_DOCUMENT_V1_UPDATED(
      "google.cloud.firestore.document.v1.updated", "Firestore Document Updated (v1)"),
  FIRESTORE_DOCUMENT_V1_WRITTEN(
      "google.cloud.firestore.document.v1.written", "Firestore Document Written (v1)"),
  PUBSUB_TOPIC_V1_MESSAGE_PUBLISHED(
      "google.cloud.pubsub.topic.v1.messagePublished", "PubSub Topic Message Published (v1)"),
  STORAGE_OBJECT_V1_FINALIZED(
      "google.cloud.storage.object.v1.finalized", "Storage Object Finalized (v1)"),
  STORAGE_OBJECT_V1_ARCHIVED(
      "google.cloud.storage.object.v1.archived", "Storage Object Archived (v1)"),
  STORAGE_OBJECT_V1_DELETED(
      "google.cloud.storage.object.v1.deleted", "Storage Object Deleted (v1)"),
  STORAGE_OBJECT_V1_METADATAUPDATED(
      "google.cloud.storage.object.v1.metadataUpdated", "Storage Object Metadata Updated (v1)");

  private String eType;
  private String eDesc;

  private CloudEventType(String eType, String eDesc) {
    this.eType = eType;
    this.eDesc = eDesc;
  }

  public static CloudEventType fromCode(String eType) {
    for (int i = 0; i < CloudEventType.class.getEnumConstants().length; i++)
      if (CloudEventType.class.getEnumConstants()[i].getCode().equals(eType))
        return CloudEventType.class.getEnumConstants()[i];
    return null;
  }

  public static CloudEventType fromDescription(String eDesc) {
    for (int i = 0; i < CloudEventType.class.getEnumConstants().length; i++)
      if (CloudEventType.class.getEnumConstants()[i].getDesc().equals(eDesc))
        return CloudEventType.class.getEnumConstants()[i];
    return null;
  }

  public static CloudEventType[] toArray() {
    return CloudEventType.class.getEnumConstants();
  }

  public String getCode() {
    return eType;
  }

  public String getDesc() {
    return eDesc;
  }
}
