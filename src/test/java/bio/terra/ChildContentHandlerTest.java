package bio.terra;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import bio.terra.cloudfunctions.common.ContentHandler;
import bio.terra.cloudfunctions.common.GsonWrapper;
import bio.terra.common.BaseTest;
import com.google.events.cloud.storage.v1.StorageObjectData;
import java.util.logging.Logger;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.junit.Test;

public class ChildContentHandlerTest extends BaseTest {
  private static final Logger logger = Logger.getLogger(ChildContentHandlerTest.class.getName());

  @Test
  public void acceptEventTest() {

    try {
      StorageObjectData storageObjectData =
          GsonWrapper.convertFromClass(MOCK_EVENT_GZIP, StorageObjectData.class);
      ChildContentHandler handler = new ChildContentHandler(storageObjectData);
      handler.setInputStream(MOCK_TGZ);
      handler.handleMediaType();
      ArchiveInputStream ais = (ArchiveInputStream) handler.getDataStream();
      ArchiveEntry archiveEntry;
      logger.info("mockTGZTest:");
      int numberOfFilesProcessed = 0;
      while ((archiveEntry = ais.getNextEntry()) != null) {
        logger.info("Verifying " + archiveEntry.getName() + " filesize.");
        if (!archiveEntry.isDirectory()) {
          numberOfFilesProcessed++;
          verifyMockTGZArchiveEntry(archiveEntry.getName(), archiveEntry.getSize());
        }
      }
      assertEquals(3, numberOfFilesProcessed);
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  static class ChildContentHandler extends ContentHandler {
    public ChildContentHandler(StorageObjectData data) {
      super(data);
    }

    @Override
    public void translate() throws UnsupportedOperationException {
      throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void insert() throws UnsupportedOperationException {
      throw new UnsupportedOperationException("Not implemented");
    }
  }
}
