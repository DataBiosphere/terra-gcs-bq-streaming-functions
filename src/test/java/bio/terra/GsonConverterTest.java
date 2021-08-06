package bio.terra;

import bio.terra.cloudfunctions.utils.GsonConverter;
import bio.terra.common.BaseTest;
import com.google.events.cloud.storage.v1.StorageObjectData;
import org.junit.Test;

public class GsonConverterTest extends BaseTest {
  @Test
  public void parseStorageObjectData() {
    StorageObjectData data =
        GsonConverter.convertFromClass(MOCK_EVENT_GZIP, StorageObjectData.class);
    assertStorageObjectData(data);
  }
}
