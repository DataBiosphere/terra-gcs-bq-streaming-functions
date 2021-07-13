package bio.terra;

import bio.terra.cloudfunctions.common.ContentHandler;
import bio.terra.cloudfunctions.common.GsonWrapper;
import bio.terra.cloudfunctions.utils.MediaTypeUtils;
import bio.terra.common.BaseTest;
import com.google.events.cloud.storage.v1.StorageObjectData;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.junit.Test;

public class ChildContentHandlerTest extends BaseTest {
  @Test
  public void acceptEventTest() {
    ChildContentHandler handler = new ChildContentHandler();
    try {
      handler.setInputStream(MOCK_TGZ);
      handler.accept(MOCK_EVENT_GZIP, FAKE_CLOUD_FUNCTION_CONTEXT);
      assertEvent(handler.getEvent());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void acceptCustomEventTest() {
    ChildContentHandlerWithCustomEventData handler = new ChildContentHandlerWithCustomEventData();
    try {
      handler.setInputStream(MOCK_TGZ_DYNAMIC_BQ);
      handler.accept(MOCK_EVENT_GZIP, FAKE_CLOUD_FUNCTION_CONTEXT);
      handler.translate();
      assertCustomEvent(handler.getEvent());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  class ChildContentHandler extends ContentHandler<StorageObjectData> {
    @Override
    public void translate() throws Exception {
      super.translate();
    }

    @Override
    public void insert(String table, byte[] data) throws Exception {
      super.insert(table, data);
    }
  }

  class ChildContentHandlerWithCustomEventData extends ContentHandler<CustomEventData> {
    @Override
    public void translate() throws Exception {
      ArchiveInputStream ais = (ArchiveInputStream) getDataStream();
      ArchiveEntry archiveEntry;
      while ((archiveEntry = ais.getNextEntry()) != null) {
        if (!archiveEntry.isDirectory() && archiveEntry.getName().contains("dynamic")) {
          byte[] customData = MediaTypeUtils.readEntry(ais, archiveEntry.getSize());
          CustomEventData customEventData =
              GsonWrapper.getInstance().fromJson(new String(customData), CustomEventData.class);
          getEvent().setDataset(customEventData.getDataset());
          getEvent().setTables(customEventData.getTables());
        }
      }
    }

    @Override
    public void insert(String table, byte[] data) throws Exception {
      super.insert(table, data);
    }
  }
}
