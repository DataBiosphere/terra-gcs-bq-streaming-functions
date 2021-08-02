package bio.terra.cloudfiletodatastore;

import java.time.OffsetDateTime;

/**
 * Includes all the info we need from the Google file upload event and decouples us from the Google
 * realm. All business logic should reference this class rather than Google classes.
 */
public class FileUploadedMessage {

  private final String resourceName;

  private final String sourceBucket;

  private final long size;

  private final OffsetDateTime createdAt;

  private final String contentType;

  public FileUploadedMessage(
      String resourceName,
      String sourceBucket,
      long size,
      OffsetDateTime createdAt,
      String contentType) {
    this.resourceName = resourceName;
    this.sourceBucket = sourceBucket;
    this.size = size;
    this.createdAt = createdAt;
    this.contentType = contentType;
  }

  public String getResourceName() {
    return resourceName;
  }

  public String getSourceBucket() {
    return sourceBucket;
  }

  public long getSize() {
    return size;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public String getContentType() {
    return contentType;
  }

  @Override
  public String toString() {
    return "FileMessage{"
        + "resourceName='"
        + resourceName
        + '\''
        + ", sourceBucket='"
        + sourceBucket
        + '\''
        + ", size="
        + size
        + ", createdAt="
        + createdAt
        + ", contentType='"
        + contentType
        + '\''
        + '}';
  }
}
