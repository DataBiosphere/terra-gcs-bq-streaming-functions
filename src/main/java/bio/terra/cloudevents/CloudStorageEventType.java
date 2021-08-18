package bio.terra.cloudevents;

/**
 * Enums of Cloud Storage Event Types (Google, AWS, or Azure). Currently, only Google Storage Event
 * Types are implemented.
 *
 * <p>For an enumeration of all Google Cloud Event types, please refer to
 * https://cloud.google.com/functions/docs/calling/storage
 */
public enum CloudStorageEventType {
  GOOGLE_STORAGE_OBJECT_FINALIZE(
      "google.storage.object.finalize", "Google Storage Object Finalize");

  private String eType;
  private String eDesc;

  CloudStorageEventType(String eType, String eDesc) {
    this.eType = eType;
    this.eDesc = eDesc;
  }

  public static CloudStorageEventType fromCode(String eType) {
    for (int i = 0; i < CloudStorageEventType.class.getEnumConstants().length; i++)
      if (CloudStorageEventType.class.getEnumConstants()[i].getCode().equals(eType))
        return CloudStorageEventType.class.getEnumConstants()[i];
    return null;
  }

  public static CloudStorageEventType[] toArray() {
    return CloudStorageEventType.class.getEnumConstants();
  }

  public String getCode() {
    return eType;
  }

  public String getDesc() {
    return eDesc;
  }
}
