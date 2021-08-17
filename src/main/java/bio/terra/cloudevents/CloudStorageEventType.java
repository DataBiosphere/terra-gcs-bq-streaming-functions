package bio.terra.cloudevents;

/**
 * For a enumeration of all Cloud Event types, please refer to
 * https://cloud.google.com/functions/docs/calling/storage
 */
public enum CloudStorageEventType {
  GOOGLE_STORAGE_OBJECT_FINALIZE(
      "google.storage.object.finalize", "Google Storage Object Finalize"),
  GOOGLE_STORAGE_OBJECT_ARCHIVE("google.storage.object.archive", "Google Storage Object Archive"),
  GOOGLE_STORAGE_OBJECT_DELETE("google.storage.object.delete", "Google Storage Object Delete"),
  GOOGLE_STORAGE_OBJECT_V1_METADATAUPDATED(
      "google.storage.object.metadataUpdate", "Google Storage Object Metadata Update");

  private String eType;
  private String eDesc;

  private CloudStorageEventType(String eType, String eDesc) {
    this.eType = eType;
    this.eDesc = eDesc;
  }

  public static CloudStorageEventType fromCode(String eType) {
    for (int i = 0; i < CloudStorageEventType.class.getEnumConstants().length; i++)
      if (CloudStorageEventType.class.getEnumConstants()[i].getCode().equals(eType))
        return CloudStorageEventType.class.getEnumConstants()[i];
    return null;
  }

  public static CloudStorageEventType fromDescription(String eDesc) {
    for (int i = 0; i < CloudStorageEventType.class.getEnumConstants().length; i++)
      if (CloudStorageEventType.class.getEnumConstants()[i].getDesc().equals(eDesc))
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
