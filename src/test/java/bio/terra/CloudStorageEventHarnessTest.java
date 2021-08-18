package bio.terra;

import static org.junit.Assert.fail;

import bio.terra.cloudfunctions.common.GsonWrapper;
import bio.terra.common.BaseTest;
import com.google.events.cloud.storage.v1.StorageObjectData;
import java.util.Map;
import org.junit.Test;

public class CloudStorageEventHarnessTest extends BaseTest {
  @Test
  public void acceptTest() {
    try {
      GCSEventHarnessImpl instance = new GCSEventHarnessImpl();
      Map<?, ?> m = GsonWrapper.convertFromClass(MOCK_EVENT_GZIP, Map.class);
      instance.accept(m, new CFContext());
      StorageObjectData event = instance.getEvent(StorageObjectData.class);
      assertStorageObjectEvent(event);
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }
}
