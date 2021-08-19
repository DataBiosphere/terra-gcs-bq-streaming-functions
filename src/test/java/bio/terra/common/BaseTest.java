package bio.terra.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import bio.terra.cloudfunctions.common.GoogleCloudStorageEventHarness;
import bio.terra.cloudfunctions.common.GsonWrapper;
import bio.terra.cloudfunctions.common.MediaTypeWrapper;
import com.google.cloud.functions.Context;
import com.google.events.cloud.storage.v1.StorageObjectData;
import java.io.InputStream;
import java.util.logging.Logger;
import org.junit.Before;

public class BaseTest {
  private static final Logger logger = Logger.getLogger(BaseTest.class.getName());

  protected static String MOCK_EVENT_GZIP;
  protected static String MOCK_EVENT_JSON;
  protected static SupportedCloudEventContext SUPPORTED_CLOUD_EVENT_CONTEXT;
  protected static UnSupportedCloudEventContext UNSUPPORTED_CLOUD_EVENT_CONTEXT;
  protected static MediaTypeWrapper APPLICATION_GZIP;
  protected static MediaTypeWrapper APPLICATION_X_GZIP;
  protected static MediaTypeWrapper APPLICATION_JSON;
  protected static InputStream MOCK_TGZ;
  protected static InputStream MOCK_GZ;
  protected static InputStream MOCK_JSON;
  protected static byte[] MOCK_STORAGE_OBJECT_DATASTREAM;

  @Before
  public void setUp() {
    try {
      MOCK_EVENT_GZIP =
          new String(
              getClass()
                  .getClassLoader()
                  .getResourceAsStream("testfiles/mock_event_gzip.json")
                  .readAllBytes());

      MOCK_EVENT_JSON =
          new String(
              getClass()
                  .getClassLoader()
                  .getResourceAsStream("testfiles/mock_event_json.json")
                  .readAllBytes());

      // The file resources/testfiles/mock_storage_object_data.json is a byte[] array
      // equivalent to the content of resources/testfiles/mock_event_gzip.json
      MOCK_STORAGE_OBJECT_DATASTREAM =
          GsonWrapper.convertFromClass(
              new String(
                  getClass()
                      .getClassLoader()
                      .getResourceAsStream("testfiles/mock_storage_object_data.json")
                      .readAllBytes()),
              byte[].class);

      SUPPORTED_CLOUD_EVENT_CONTEXT = new SupportedCloudEventContext();
      UNSUPPORTED_CLOUD_EVENT_CONTEXT = new UnSupportedCloudEventContext();
      APPLICATION_GZIP = new MediaTypeWrapper("application/gzip");
      APPLICATION_X_GZIP = new MediaTypeWrapper("application/x-gzip");
      APPLICATION_JSON = new MediaTypeWrapper("application/json");
      MOCK_TGZ = getClass().getClassLoader().getResourceAsStream("testfiles/mock_results.tar.gz");
      MOCK_GZ =
          getClass().getClassLoader().getResourceAsStream("testfiles/SUMMARY_testRun.json.gz");
      MOCK_JSON = getClass().getClassLoader().getResourceAsStream("testfiles/SUMMARY_testRun.json");
    } catch (Exception e) {
      logger.severe(e.getMessage());
    }
  }

  /**
   * @param data - the hardcoded values came from the file resources/testfiles/mock_event_gzip.json
   */
  public void assertMockTGZStorageObjectData(StorageObjectData data) {
    // Check String deserialization
    assertEquals("terra-kernel-k8s-testrunner-results", data.getBucket());
    // Check OffsetDateTime deserialization
    assertEquals("2021-07-07T22:57:14.257Z", data.getTimeCreated().toString());
    // Check CustomerEncryption deserialization
    assertEquals(
        "6ae5555bcdd2681f1c1e5e5721b6d18a82d0aaada01d1295c22268955e9b79d4753c2f1f5f15c6e1ac80b405e366fda2f4ae26fa7390605c802b8ed7cc787c4ec8458f04c9b07fb65cea1e4344644a33bde9ce28d2eae70ff85cbebc45d6d44c7599cbebfe0b6fed5a1f275968efd29a49aedbd0fd93296f03457ebcc72b2c13",
        data.getCustomerEncryption().getKeySha256());
    // Check Map deserialization
    assertEquals("value1", data.getMetadata().get("key1"));
    assertEquals("value2", data.getMetadata().get("key2"));
  }

  public void verifyMockTGZArchiveEntry(String filename, long bytes) {
    if (filename.contains("RENDERED")) {
      assertEquals(3584, bytes);
    } else if (filename.contains("RAWDATA")) {
      assertEquals(4579, bytes);
    } else if (filename.contains("SUMMARY")) {
      assertEquals(1350, bytes);
    } else {
      fail("Unexpected " + filename);
    }
  }

  public static class UnSupportedCloudEventContext implements Context {

    @Override
    public String eventId() {
      return null;
    }

    @Override
    public String timestamp() {
      return null;
    }

    @Override
    public String eventType() {
      return "unsupported.event";
    }

    @Override
    public String resource() {
      return null;
    }
  }

  public static class SupportedCloudEventContext implements Context {

    @Override
    public String eventId() {
      return null;
    }

    @Override
    public String timestamp() {
      return null;
    }

    @Override
    public String eventType() {
      return "google.storage.object.finalize";
    }

    @Override
    public String resource() {
      return null;
    }
  }

  public static class GCSEventHarnessImpl extends GoogleCloudStorageEventHarness {

    @Override
    public void doAccept() {}
  }
}
