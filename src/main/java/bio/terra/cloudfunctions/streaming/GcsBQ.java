package bio.terra.cloudfunctions.streaming;

import bio.terra.cloudfunctions.streaming.GcsBQ.GCSEvent;
import com.google.cloud.functions.BackgroundFunction;
import com.google.cloud.functions.Context;
import java.util.Date;
import java.util.logging.Logger;

public class GcsBQ implements BackgroundFunction<GCSEvent> {
  private static final Logger logger = Logger.getLogger(GcsBQ.class.getName());

  @Override
  public void accept(GCSEvent event, Context context) throws Exception {
    System.out.println("Event: " + context.eventId());
    logger.info("Event: " + context.eventId());
    logger.info("Event Type: " + context.eventType());
    logger.info("Bucket: " + event.getBucket());
    logger.info("Processing File: " + event.getName());
    logger.info("Metageneration: " + event.getMetageneration());
    logger.info("Created: " + event.getTimeCreated());
    logger.info("Updated: " + event.getUpdated());
    logger.info("Media download link: " + event.getMediaLink());
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public static class GCSEvent {
    String id;
    String selfLink;
    String name;
    String bucket;
    Long generation;
    Long metageneration;
    String contentType;
    Date timeCreated;
    Date updated;
    Date customTime;
    Date timeDeleted;
    Boolean temporaryHold;
    Boolean eventBasedHold;
    Date retentionExpirationTime;
    String storageClass;
    Date timeStorageClassUpdated;
    Long size;
    String md5Hash;
    String mediaLink;
    String contentEncoding;
    String contentDisposition;
    String contentLanguage;
    String cacheControl;
    String crc32c;
    Integer componentCount;
    String etag;
    String kmsKeyName;

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public String getSelfLink() {
      return selfLink;
    }

    public void setSelfLink(String selfLink) {
      this.selfLink = selfLink;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getBucket() {
      return bucket;
    }

    public void setBucket(String bucket) {
      this.bucket = bucket;
    }

    public Long getGeneration() {
      return generation;
    }

    public void setGeneration(Long generation) {
      this.generation = generation;
    }

    public Long getMetageneration() {
      return metageneration;
    }

    public void setMetageneration(Long metageneration) {
      this.metageneration = metageneration;
    }

    public String getContentType() {
      return contentType;
    }

    public void setContentType(String contentType) {
      this.contentType = contentType;
    }

    public Date getTimeCreated() {
      return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
      this.timeCreated = timeCreated;
    }

    public Date getUpdated() {
      return updated;
    }

    public void setUpdated(Date updated) {
      this.updated = updated;
    }

    public Date getCustomTime() {
      return customTime;
    }

    public void setCustomTime(Date customTime) {
      this.customTime = customTime;
    }

    public Date getTimeDeleted() {
      return timeDeleted;
    }

    public void setTimeDeleted(Date timeDeleted) {
      this.timeDeleted = timeDeleted;
    }

    public Boolean getTemporaryHold() {
      return temporaryHold;
    }

    public void setTemporaryHold(Boolean temporaryHold) {
      this.temporaryHold = temporaryHold;
    }

    public Boolean getEventBasedHold() {
      return eventBasedHold;
    }

    public void setEventBasedHold(Boolean eventBasedHold) {
      this.eventBasedHold = eventBasedHold;
    }

    public Date getRetentionExpirationTime() {
      return retentionExpirationTime;
    }

    public void setRetentionExpirationTime(Date retentionExpirationTime) {
      this.retentionExpirationTime = retentionExpirationTime;
    }

    public String getStorageClass() {
      return storageClass;
    }

    public void setStorageClass(String storageClass) {
      this.storageClass = storageClass;
    }

    public Date getTimeStorageClassUpdated() {
      return timeStorageClassUpdated;
    }

    public void setTimeStorageClassUpdated(Date timeStorageClassUpdated) {
      this.timeStorageClassUpdated = timeStorageClassUpdated;
    }

    public Long getSize() {
      return size;
    }

    public void setSize(Long size) {
      this.size = size;
    }

    public String getMd5Hash() {
      return md5Hash;
    }

    public void setMd5Hash(String md5Hash) {
      this.md5Hash = md5Hash;
    }

    public String getMediaLink() {
      return mediaLink;
    }

    public void setMediaLink(String mediaLink) {
      this.mediaLink = mediaLink;
    }

    public String getContentEncoding() {
      return contentEncoding;
    }

    public void setContentEncoding(String contentEncoding) {
      this.contentEncoding = contentEncoding;
    }

    public String getContentDisposition() {
      return contentDisposition;
    }

    public void setContentDisposition(String contentDisposition) {
      this.contentDisposition = contentDisposition;
    }

    public String getContentLanguage() {
      return contentLanguage;
    }

    public void setContentLanguage(String contentLanguage) {
      this.contentLanguage = contentLanguage;
    }

    public String getCacheControl() {
      return cacheControl;
    }

    public void setCacheControl(String cacheControl) {
      this.cacheControl = cacheControl;
    }

    public String getCrc32c() {
      return crc32c;
    }

    public void setCrc32c(String crc32c) {
      this.crc32c = crc32c;
    }

    public Integer getComponentCount() {
      return componentCount;
    }

    public void setComponentCount(Integer componentCount) {
      this.componentCount = componentCount;
    }

    public String getEtag() {
      return etag;
    }

    public void setEtag(String etag) {
      this.etag = etag;
    }

    public String getKmsKeyName() {
      return kmsKeyName;
    }

    public void setKmsKeyName(String kmsKeyName) {
      this.kmsKeyName = kmsKeyName;
    }
  }
}
