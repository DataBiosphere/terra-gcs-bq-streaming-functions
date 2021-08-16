package bio.terra;

import static org.junit.Assert.fail;

import bio.terra.cloudevents.GCSEvent;
import bio.terra.cloudfunctions.common.GsonWrapper;
import bio.terra.common.BaseTest;
import org.junit.Test;

public class CloudStorageEventHarnessTest extends BaseTest {
  @Test
  public void acceptTest() {
    try {
      GCSEventHarnessImpl instance = new GCSEventHarnessImpl();
      instance.accept(
          GsonWrapper.convertFromClass(MOCK_EVENT_GZIP, GCSEvent.class), new CFContext());
      GCSEvent event = instance.getEvent();
      assertGCSEvent(event);
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }
}
