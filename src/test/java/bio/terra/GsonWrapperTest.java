package bio.terra;

import static org.junit.Assert.fail;

import bio.terra.cloudfunctions.common.GsonWrapper;
import bio.terra.common.BaseTest;
import com.google.events.cloud.storage.v1.StorageObjectData;
import org.junit.Test;

public class GsonWrapperTest extends BaseTest {

  @Test
  public void parseStorageObjectData() {
    try {
      StorageObjectData data =
          GsonWrapper.convertFromClass(MOCK_EVENT_GZIP, StorageObjectData.class);
      assertMockTGZStorageObjectData(data);
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }
}
