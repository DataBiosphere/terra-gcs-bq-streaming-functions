package bio.terra;

import bio.terra.cloudfunctions.common.GsonWrapper;
import bio.terra.common.BaseTest;
import com.google.events.cloud.storage.v1.StorageObjectData;
import org.junit.Test;

public class GsonWrapperTest extends BaseTest {
  @Test
  public void parseStorageObjectData() {
    StorageObjectData event =
        GsonWrapper.getInstance().fromJson(MOCK_EVENT_GZIP, StorageObjectData.class);
    assertEvent(event);
  }
}
