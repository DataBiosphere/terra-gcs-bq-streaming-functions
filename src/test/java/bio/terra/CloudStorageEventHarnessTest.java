package bio.terra;

import static org.junit.Assert.fail;

import bio.terra.cloudevents.GCSEvent;
import bio.terra.cloudfunctions.common.GsonWrapper;
import bio.terra.common.BaseTest;
import com.google.gson.internal.LinkedTreeMap;
import org.junit.Test;

public class CloudStorageEventHarnessTest extends BaseTest {
  @Test
  public void acceptTest() {
    try {
      GCSEventHarnessImpl instance = new GCSEventHarnessImpl();
      LinkedTreeMap m = GsonWrapper.convertFromClass(MOCK_EVENT_GZIP, LinkedTreeMap.class);
      instance.accept(m, new CFContext());
      GCSEvent event = instance.getEvent(GCSEvent.class);
      assertGCSEvent(event);
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }
}
