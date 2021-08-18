package bio.terra.cloudfunctions.common;

import bio.terra.cloudevents.CloudStorageEventType;

/**
 * AppReceiver receives a set of properties that can be used to determine how and whether to process
 * the event.
 */
public class AppReceiver {
  private CloudStorageEventType cloudStorageEventType;
  private String contentType;
  private String bucket;
  private String name;

  public CloudStorageEventType getCloudStorageEventType() {
    return cloudStorageEventType;
  }

  public void setCloudStorageEventType(CloudStorageEventType cloudStorageEventType) {
    this.cloudStorageEventType = cloudStorageEventType;
  }

  public String getContentType() {
    return contentType;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public String getBucket() {
    return bucket;
  }

  public void setBucket(String bucket) {
    this.bucket = bucket;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
