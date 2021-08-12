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

  public FileUploadedMessage(
      String resourceName, String sourceBucket, long size, OffsetDateTime createdAt) {
    this.resourceName = resourceName;
    this.sourceBucket = sourceBucket;
    this.size = size;
    this.createdAt = createdAt;
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

  @Override
  public String toString() {
    return "FileUploadedMessage{"
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
        + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof FileUploadedMessage)) return false;

    FileUploadedMessage that = (FileUploadedMessage) o;

    if (getSize() != that.getSize()) return false;
    if (getResourceName() != null
        ? !getResourceName().equals(that.getResourceName())
        : that.getResourceName() != null) return false;
    if (getSourceBucket() != null
        ? !getSourceBucket().equals(that.getSourceBucket())
        : that.getSourceBucket() != null) return false;
    return getCreatedAt() != null
        ? getCreatedAt().equals(that.getCreatedAt())
        : that.getCreatedAt() == null;
  }

  @Override
  public int hashCode() {
    int result = getResourceName() != null ? getResourceName().hashCode() : 0;
    result = 31 * result + (getSourceBucket() != null ? getSourceBucket().hashCode() : 0);
    result = 31 * result + (int) (getSize() ^ (getSize() >>> 32));
    result = 31 * result + (getCreatedAt() != null ? getCreatedAt().hashCode() : 0);
    return result;
  }
}
