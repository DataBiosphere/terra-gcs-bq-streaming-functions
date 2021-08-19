package bio.terra;

import bio.terra.cloudfunctions.common.GsonWrapper;
import bio.terra.common.BaseTest;
import com.google.events.cloud.storage.v1.StorageObjectData;
import java.util.logging.Logger;
import org.junit.Test;

public class GsonWrapperTest extends BaseTest {
  private static final Logger logger = Logger.getLogger(GsonWrapperTest.class.getName());

  @Test
  public void parseStorageObjectData() {
    try {
      StorageObjectData data =
          GsonWrapper.convertFromClass(MOCK_EVENT_GZIP, StorageObjectData.class);
      assertMockTGZStorageObjectData(data);
    } catch (Exception e) {
      logger.severe(e.getMessage());
    }
  }
}
