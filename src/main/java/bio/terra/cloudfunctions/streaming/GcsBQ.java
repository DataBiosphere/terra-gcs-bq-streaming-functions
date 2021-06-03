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
    logger.debug("Event: " + context.eventId());
    logger.debug("Event Type: " + context.eventType());
    logger.debug("Bucket: " + event.getBucket());
    logger.debug("Processing File: " + event.getName());
    logger.debug("Metageneration: " + event.getMetageneration());
    logger.debug("Created: " + event.getTimeCreated());
    logger.debug("Updated: " + event.getUpdated());
    logger.debug("Media download link: " + event.getMediaLink());
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public static class GCSEvent {
    String bucket;
    String name;
    Long metageneration;
    Date timeCreated;
    Date updated;

    public String getBucket() {
      return bucket;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public Long getMetageneration() {
      return metageneration;
    }

    public void setMetageneration(Long metageneration) {
      this.metageneration = metageneration;
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

    public String getMediaLink() {
      return mediaLink;
    }

    public void setMediaLink(String mediaLink) {
      this.mediaLink = mediaLink;
    }

    public void setBucket(String bucket) {
      this.bucket = bucket;
    }

    String mediaLink;
  }
}
