package bio.terra;

import bio.terra.cloudfunctions.common.ContentHandler;
import bio.terra.cloudfunctions.common.GsonWrapper;
import bio.terra.common.BaseTest;
import com.google.events.cloud.storage.v1.StorageObjectData;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.junit.Test;

public class ChildContentHandlerTest extends BaseTest {
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
      System.out.println("mockTGZTest:");
      while ((archiveEntry = ais.getNextEntry()) != null) {
        verifyMockTGZArchiveEntry(archiveEntry.getName(), archiveEntry.getSize());
      }
    } catch (Exception e) {
    }
  }

  class ChildContentHandler extends ContentHandler {
    public ChildContentHandler(StorageObjectData data) {
      super(data);
    }

    @Override
    public void translate() throws Exception {}

    @Override
    public void insert() throws Exception {}
  }
}
