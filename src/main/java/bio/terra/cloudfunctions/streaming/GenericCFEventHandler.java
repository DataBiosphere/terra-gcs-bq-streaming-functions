package bio.terra.cloudfunctions.streaming;

import bio.terra.cloudevents.v1.CloudEventType;
import bio.terra.cloudevents.v1.messagewrapper.StorageObjectEventMessage;
import bio.terra.cloudfunctions.common.App;
import bio.terra.cloudfunctions.common.CloudEventsHarness;
import bio.terra.cloudfunctions.common.ContentHandler;
import com.google.events.cloud.storage.v1.StorageObjectData;
import io.cloudevents.CloudEvent;
import java.util.logging.Logger;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;

public class GenericCFEventHandler extends CloudEventsHarness {
  private static final Logger logger = Logger.getLogger(GenericCFEventHandler.class.getName());
  private GenericApp app;

  @Override
  public void accept(CloudEvent event) throws Exception {
    super.accept(event);
    app = new GenericApp(getCloudEventType(), getMessage());
    app.process();
  }

  class GenericApp extends App {
    public GenericApp(CloudEventType eventType, Object message) {
      super(eventType, message);
    }

    @Override
    public void process() throws Exception {
      if (CloudEventType.STORAGE_OBJECT_V1_FINALIZED.getCode().equals(eventType.getCode())) {
        StorageObjectData storageObjectData =
            ((StorageObjectEventMessage) getMessage()).getMessage();
        logger.info(
            String.format(
                "Received %s event from bucket %s of content type %s",
                getEventType().getDesc(),
                storageObjectData.getBucket(),
                storageObjectData.getContentType()));
        GenericContentHandler contentHandler = new GenericContentHandler(storageObjectData);
        contentHandler.handleMediaType();
      }
    }
  }

  class GenericContentHandler extends ContentHandler {
    public GenericContentHandler(StorageObjectData storageObjectData) {
      super(storageObjectData);
    }

    @Override
    public void translate() throws Exception {
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
