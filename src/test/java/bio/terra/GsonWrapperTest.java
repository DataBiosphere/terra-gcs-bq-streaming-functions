package bio.terra;

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
      assertStorageObjectData(data);
    } catch (Exception e) {
    }
  }
}
