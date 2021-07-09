package bio.terra.common;

import static org.junit.Assert.assertTrue;

import bio.terra.cloudfunctions.common.MediaTypeWrapper;
import com.google.cloud.functions.Context;
import com.google.events.cloud.storage.v1.StorageObjectData;
import java.io.InputStream;
import org.junit.Before;

public class BaseTest {
  protected static String MOCK_EVENT_GZIP;
  protected static String MOCK_EVENT_JSON;
  protected static CFContext FAKE_CLOUD_FUNCTION_CONTEXT;
  protected static MediaTypeWrapper APPLICATION_GZIP;
  protected static MediaTypeWrapper APPLICATION_X_GZIP;
  protected static MediaTypeWrapper APPLICATION_JSON;
  protected static InputStream MOCK_TGZ;
  protected static InputStream MOCK_GZ;
  protected static InputStream MOCK_JSON;

  @Before
  public void setUp() {
    MOCK_EVENT_GZIP =
        "{\"bucket\":\"terra-kernel-k8s-testrunner-results\",\"contentType\":\"application/gzip\",\"crc32c\":\"NzTz+Q\\u003d\\u003d\",\"etag\":\"CM6c6KyH0vECEAE\\u003d\",\"generation\":\"1625698634174030\",\"id\":\"terra-kernel-k8s-testrunner-results/390ef56b-943f-43ab-92f2-73c6ca7dda22.tar.gz/1625698634174030\",\"kind\":\"storage#object\",\"md5Hash\":\"vYfIYE1pVfutUJgz7hbWwQ\\u003d\\u003d\",\"mediaLink\":\"https://www.googleapis.com/download/storage/v1/b/terra-kernel-k8s-testrunner-results/o/390ef56b-943f-43ab-92f2-73c6ca7dda22.tar.gz?generation\\u003d1625698634174030\\u0026alt\\u003dmedia\",\"metageneration\":\"1\",\"name\":\"390ef56b-943f-43ab-92f2-73c6ca7dda22.tar.gz\",\"selfLink\":\"https://www.googleapis.com/storage/v1/b/terra-kernel-k8s-testrunner-results/o/390ef56b-943f-43ab-92f2-73c6ca7dda22.tar.gz\",\"size\":\"2133\",\"storageClass\":\"STANDARD\",\"timeCreated\":\"2021-07-07T22:57:14.257Z\",\"timeStorageClassUpdated\":\"2021-07-07T22:57:14.257Z\",\"updated\":\"2021-07-07T22:57:14.257Z\",\"customerEncryption\":{\"encryptionAlgorithm\":\"AES256\",\"keySha256\":\"6ae5555bcdd2681f1c1e5e5721b6d18a82d0aaada01d1295c22268955e9b79d4753c2f1f5f15c6e1ac80b405e366fda2f4ae26fa7390605c802b8ed7cc787c4ec8458f04c9b07fb65cea1e4344644a33bde9ce28d2eae70ff85cbebc45d6d44c7599cbebfe0b6fed5a1f275968efd29a49aedbd0fd93296f03457ebcc72b2c13\"},\"metadata\":{\"key1\":\"value1\",\"key2\":\"value2\"}}";
    MOCK_EVENT_JSON =
        "{\"bucket\":\"terra-kernel-k8s-testrunner-results\",\"contentType\":\"application/json\",\"crc32c\":\"NzTz+Q\\u003d\\u003d\",\"etag\":\"CM6c6KyH0vECEAE\\u003d\",\"generation\":\"1625698634174030\",\"id\":\"terra-kernel-k8s-testrunner-results/390ef56b-943f-43ab-92f2-73c6ca7dda22.tar.gz/1625698634174030\",\"kind\":\"storage#object\",\"md5Hash\":\"vYfIYE1pVfutUJgz7hbWwQ\\u003d\\u003d\",\"mediaLink\":\"https://www.googleapis.com/download/storage/v1/b/terra-kernel-k8s-testrunner-results/o/390ef56b-943f-43ab-92f2-73c6ca7dda22.tar.gz?generation\\u003d1625698634174030\\u0026alt\\u003dmedia\",\"metageneration\":\"1\",\"name\":\"390ef56b-943f-43ab-92f2-73c6ca7dda22.tar.gz\",\"selfLink\":\"https://www.googleapis.com/storage/v1/b/terra-kernel-k8s-testrunner-results/o/390ef56b-943f-43ab-92f2-73c6ca7dda22.tar.gz\",\"size\":\"2133\",\"storageClass\":\"STANDARD\",\"timeCreated\":\"2021-07-07T22:57:14.257Z\",\"timeStorageClassUpdated\":\"2021-07-07T22:57:14.257Z\",\"updated\":\"2021-07-07T22:57:14.257Z\",\"customerEncryption\":{\"encryptionAlgorithm\":\"AES256\",\"keySha256\":\"6ae5555bcdd2681f1c1e5e5721b6d18a82d0aaada01d1295c22268955e9b79d4753c2f1f5f15c6e1ac80b405e366fda2f4ae26fa7390605c802b8ed7cc787c4ec8458f04c9b07fb65cea1e4344644a33bde9ce28d2eae70ff85cbebc45d6d44c7599cbebfe0b6fed5a1f275968efd29a49aedbd0fd93296f03457ebcc72b2c13\"},\"metadata\":{\"key1\":\"value1\",\"key2\":\"value2\"}}";
    FAKE_CLOUD_FUNCTION_CONTEXT = new CFContext();
    APPLICATION_GZIP = new MediaTypeWrapper("application/gzip");
    APPLICATION_X_GZIP = new MediaTypeWrapper("application/x-gzip");
    APPLICATION_JSON = new MediaTypeWrapper("application/json");
    MOCK_TGZ = getClass().getClassLoader().getResourceAsStream("testfiles/mock_results.tar.gz");
    MOCK_GZ = getClass().getClassLoader().getResourceAsStream("testfiles/SUMMARY_testRun.json.gz");
    MOCK_JSON = getClass().getClassLoader().getResourceAsStream("testfiles/SUMMARY_testRun.json");
  }

  public void assertEvent(StorageObjectData event) {
    // Check String deserialization
    assertTrue("terra-kernel-k8s-testrunner-results".equals(event.getBucket()));
    // Check OffsetDateTime deserialization
    assertTrue("2021-07-07T22:57:14.257Z".equals(event.getTimeCreated().toString()));
    // Check CustomerEncryption deserialization
    assertTrue(
        "6ae5555bcdd2681f1c1e5e5721b6d18a82d0aaada01d1295c22268955e9b79d4753c2f1f5f15c6e1ac80b405e366fda2f4ae26fa7390605c802b8ed7cc787c4ec8458f04c9b07fb65cea1e4344644a33bde9ce28d2eae70ff85cbebc45d6d44c7599cbebfe0b6fed5a1f275968efd29a49aedbd0fd93296f03457ebcc72b2c13"
            .equals(event.getCustomerEncryption().getKeySha256()));
    // Check Map deserialization
    assertTrue("value1".equals(event.getMetadata().get("key1")));
    assertTrue("value2".equals(event.getMetadata().get("key2")));
  }

  public void verifyMockTGZArchiveEntry(String filename, long bytes) {
    System.out.println("Verifying " + filename + " filesize.");
    if (filename.contains("RENDERED")) {
      assertTrue(bytes == 3584);
    } else if (filename.contains("RAWDATA")) {
      assertTrue(bytes == 4579);
    } else if (filename.contains("SUMMARY")) {
      assertTrue(bytes == 1350);
    }
  }

  class CFContext implements Context {

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
      return null;
    }

    @Override
    public String resource() {
      return null;
    }
  }
}
