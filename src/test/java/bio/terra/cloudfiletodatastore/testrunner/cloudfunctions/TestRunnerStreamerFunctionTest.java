package bio.terra.cloudfiletodatastore.testrunner.cloudfunctions;

import bio.terra.cloudfunctions.common.GsonWrapper;
import bio.terra.common.BaseTest;
import com.google.gson.internal.LinkedTreeMap;
import java.util.Map;
import org.junit.Test;

public class TestRunnerStreamerFunctionTest extends BaseTest {
  @Test
  public void processMessageTest() throws Exception {
    GCSCloudEventContext context = GCS_CLOUD_EVENT_CONTEXT;
    Map<?, ?> event = GsonWrapper.convertFromClass(MOCK_GCS_EVENT_GZIP, LinkedTreeMap.class);
    new BaseTest.MockTestRunnerStreamingFunction().accept(event, context);
  }
}
