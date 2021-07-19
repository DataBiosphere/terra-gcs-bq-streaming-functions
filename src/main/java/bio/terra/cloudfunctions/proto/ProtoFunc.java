package bio.terra.cloudfunctions.proto;

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

public class ProtoFunc extends CloudEventsHarness {
  private static final Logger logger = Logger.getLogger(ProtoFunc.class.getName());
  // Can use @Inject here if a DI framework is used (Spring or Java CDI).
  ProtoApp app;

  @Override
  public void accept(CloudEvent event) throws Exception {
    super.accept(event);
    app = new ProtoApp();
    app.setEventType(getCloudEventType());
    app.setMessage(getMessage());
    app.process();
  }

  class ProtoApp extends App {
    public ProtoApp() {
      super();
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
        ProtoContentHandler contentHandler = new ProtoContentHandler(storageObjectData);
        contentHandler.handleMediaType();
      }
    }
  }

  class ProtoContentHandler extends ContentHandler {
    public ProtoContentHandler(StorageObjectData storageObjectData) {
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
