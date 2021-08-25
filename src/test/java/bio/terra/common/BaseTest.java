package bio.terra.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import bio.terra.cloudfiletodatastore.FileUploadedMessage;
import bio.terra.cloudfiletodatastore.testrunner.cloudfunctions.TestRunnerStreamingFunction;
import bio.terra.cloudfiletodatastore.testrunner.cloudfunctions.TestRunnerStreamingProcessor;
import bio.terra.cloudfunctions.common.GoogleCloudEventHarness;
import bio.terra.cloudfunctions.common.MediaTypeWrapper;
import com.google.cloud.functions.Context;
import com.google.common.collect.ImmutableMap;
import com.google.events.cloud.storage.v1.StorageObjectData;
import java.io.InputStream;
import java.util.Map;
import java.util.logging.Logger;
import org.junit.Before;

public class BaseTest {
  private static final Logger logger = Logger.getLogger(BaseTest.class.getName());

  protected static String MOCK_GCS_EVENT_GZIP;
  protected static GCSCloudEventContext GCS_CLOUD_EVENT_CONTEXT;
  protected static UnSupportedCloudEventContext UNSUPPORTED_CLOUD_EVENT_CONTEXT;
  protected static MediaTypeWrapper APPLICATION_GZIP;
  protected static MediaTypeWrapper APPLICATION_X_GZIP;
  protected static MediaTypeWrapper APPLICATION_JSON;
  public static InputStream MOCK_TGZ;
  protected static InputStream MOCK_GZ;
  public static Map<String, String> CF_ENV;

  @Before
  public void setUp() {
    try {
      MOCK_GCS_EVENT_GZIP =
          new String(
              getClass()
                  .getClassLoader()
                  .getResourceAsStream("testfiles/mock_gcs_event_gzip.json")
                  .readAllBytes());

      GCS_CLOUD_EVENT_CONTEXT = new GCSCloudEventContext();
      UNSUPPORTED_CLOUD_EVENT_CONTEXT = new UnSupportedCloudEventContext();
      APPLICATION_GZIP = new MediaTypeWrapper("application/gzip");
      APPLICATION_X_GZIP = new MediaTypeWrapper("application/x-gzip");
      APPLICATION_JSON = new MediaTypeWrapper("application/json");
      MOCK_TGZ =
          getClass()
              .getClassLoader()
              .getResourceAsStream("testfiles/mock_testrunner_results.tar.gz");
      MOCK_GZ =
          getClass().getClassLoader().getResourceAsStream("testfiles/SUMMARY_testRun.json.gz");
      CF_ENV =
          ImmutableMap.of(
              "GCLOUD_PROJECT",
              "terra-kernel-k8s",
              "GOOGLE_BUCKET",
              "terra-kernel-k8s-testrunner-results",
              "BQ_DATASET",
              "simple_data_set",
              "BQ_TABLE",
              "SUMMARY_testRun");
    } catch (Exception e) {
      logger.severe(e.getMessage());
    }
  }

  /**
   * @param data - the hardcoded values came from the file
   *     resources/testfiles/mock_gcs_event_gzip.json
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

  public static class GCSCloudEventContext implements Context {

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

  public static class GCSEventHarnessImpl extends GoogleCloudEventHarness {

    @Override
    public void doAccept() {}
  }

  public static class MockTestRunnerStreamingProcessor extends TestRunnerStreamingProcessor {
    private static final Logger logger =
        Logger.getLogger(MockTestRunnerStreamingProcessor.class.getName());

    public MockTestRunnerStreamingProcessor(FileUploadedMessage fileUploadedMessage) {
      super(fileUploadedMessage);
    }

    @Override
    public void loadEnvVars() {
      projectId = CF_ENV.get("GCLOUD_PROJECT");
      dataSet = CF_ENV.get("BQ_DATASET");
      table = CF_ENV.get("BQ_TABLE");
    }

    @Override
    public InputStream getStorageObjectDataAsInputStream(
        String projectId, String sourceBucket, String resourceName) {
      return MOCK_TGZ;
    }

    @Override
    public void streamToBQ(String projectId, String dataset, String table, byte[] data) {
      assertEquals("terra-kernel-k8s", projectId);
      assertEquals("simple_data_set", dataset);
      assertEquals("SUMMARY_testRun", table);
      assertEquals(1350, data.length);
    }
  }

  public static class MockTestRunnerStreamingFunction extends TestRunnerStreamingFunction {
    private static final Logger logger =
        Logger.getLogger(MockTestRunnerStreamingFunction.class.getName());

    @Override
    public void loadEnvVars() {
      expectedBucket = CF_ENV.get("GOOGLE_BUCKET");
    }

    @Override
    public void processMessage(FileUploadedMessage fileUploadedMessage) {
      MockTestRunnerStreamingProcessor processor =
          new MockTestRunnerStreamingProcessor(fileUploadedMessage);
      processor.processMessage();
    }
  }
}
