package bio.terra.cloudfunctions.proto;

import bio.terra.cloudevents.v1.CloudEventType;
import bio.terra.cloudevents.v1.messagewrapper.StorageObjectEventMessage;
import bio.terra.cloudfunctions.common.App;
import bio.terra.cloudfunctions.common.ContentHandler;
import com.google.events.cloud.storage.v1.StorageObjectData;
import java.util.logging.Logger;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;

/** This class is a subclass of App with a custom implementation of the process() logic. */
public class ProtoApp extends App {
  private static final Logger logger = Logger.getLogger(ProtoApp.class.getName());

  public ProtoApp(CloudEventType eventType, Object message) {
    super(eventType, message);
  }

  // Business logic
  @Override
  public void process() throws Exception {
    if (CloudEventType.STORAGE_OBJECT_V1_FINALIZED
        .getCode()
        .equals(getCloudEventType().getCode())) {
      StorageObjectData storageObjectData = ((StorageObjectEventMessage) getMessage()).getMessage();
      logger.info(
          String.format(
              "Received %s event from bucket %s of content type %s",
              getCloudEventType().getDesc(),
              storageObjectData.getBucket(),
              storageObjectData.getContentType()));
      ProtoContentHandler contentHandler = new ProtoContentHandler(storageObjectData);
      contentHandler.handleMediaType();
    }
  }

  static class ProtoContentHandler extends ContentHandler {
    public ProtoContentHandler(StorageObjectData storageObjectData) {
      super(storageObjectData);
    }

    @Override
    public void translate() throws Exception {
      logger.info("Inside translate method for cloud events function");
      ArchiveInputStream ais = (ArchiveInputStream) getDataStream();
      ArchiveEntry archiveEntry;
      while ((archiveEntry = ais.getNextEntry()) != null) {
        if (!archiveEntry.isDirectory()) {
          logger.info(
              String.format(
                  "Invoke translate method on '%s' of %s bytes.",
                  archiveEntry.getName(), archiveEntry.getSize()));
        }
      }
    }

    @Override
    public void insert() throws Exception {
      throw new UnsupportedOperationException("insert method must be overridden by sub-classes");
    }
  }
}
