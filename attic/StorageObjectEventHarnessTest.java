package bio.terra;

import static org.junit.Assert.fail;

import bio.terra.common.BaseTest;
import com.google.events.cloud.storage.v1.StorageObjectData;
import org.junit.Test;

public class StorageObjectEventHarnessTest extends BaseTest {
    @Test
    public void acceptTest() {
        try {
            StorageObjectEventHarnessImpl instance = new StorageObjectEventHarnessImpl();
            instance.accept(MOCK_EVENT_GZIP, new CFContext());
            StorageObjectData message = instance.getMessage();
            assertStorageObjectData(message);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    public static class StorageObjectEventHarnessImpl extends StorageObjectEventHarness {

        @Override
        public void doAccept() {}
    }
}
