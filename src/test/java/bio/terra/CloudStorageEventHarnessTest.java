package bio.terra;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;

import bio.terra.cloudfunctions.common.GsonWrapper;
import bio.terra.common.BaseTest;
import com.google.events.cloud.storage.v1.StorageObjectData;
import java.util.Map;
import org.junit.Test;

public class CloudStorageEventHarnessTest extends BaseTest {
  @Test
  public void acceptSupportedCloudEventTest() {
    try {
      GCSEventHarnessImpl instance = new GCSEventHarnessImpl();
      Map<?, ?> m = GsonWrapper.convertFromClass(MOCK_GCS_EVENT_GZIP, Map.class);
      instance.accept(m, GCS_CLOUD_EVENT_CONTEXT);
      StorageObjectData event = instance.getEvent(StorageObjectData.class);
      assertMockTGZStorageObjectData(event);
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void rejectUnSupportedCloudEventTest() {
    GCSEventHarnessImpl instance = new GCSEventHarnessImpl();
    Map<?, ?> m = GsonWrapper.convertFromClass(MOCK_GCS_EVENT_GZIP, Map.class);
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          instance.accept(m, UNSUPPORTED_CLOUD_EVENT_CONTEXT);
        });
  }
}
