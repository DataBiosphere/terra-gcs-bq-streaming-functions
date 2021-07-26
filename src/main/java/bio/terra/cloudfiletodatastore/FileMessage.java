package bio.terra.cloudfiletodatastore;

import java.util.Date;

public class FileMessage {

  private final String resourceUrl;

  private final String sourceBucket;

  private final long size;

  private final Date createdAt;

  public FileMessage(String resourceUrl, String sourceBucket, long size, Date createdAt) {
    this.resourceUrl = resourceUrl;
    this.sourceBucket = sourceBucket;
    this.size = size;
    this.createdAt = createdAt;
  }

  public String getResourceUrl() {
    return resourceUrl;
  }

  public String getSourceBucket() {
    return sourceBucket;
  }

  public long getSize() {
    return size;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  @Override
  public String toString() {
    return "FileMessage{"
        + "resourceUrl='"
        + resourceUrl
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
}
