package bio.terra.common;

import static org.junit.Assert.assertEquals;

import bio.terra.cloudfunctions.common.MediaTypeWrapper;
import bio.terra.cloudfunctions.common.StorageObjectEventHarness;
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
  protected static byte[] MOCK_STORAGE_OBJECT_DATASTREAM;

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
    MOCK_STORAGE_OBJECT_DATASTREAM =
        new byte[] {
          123, 34, 98, 117, 99, 107, 101, 116, 34, 58, 34, 116, 101, 114, 114, 97, 45, 107, 101,
          114, 110, 101, 108, 45, 107, 56, 115, 45, 116, 101, 115, 116, 114, 117, 110, 110, 101,
          114, 45, 114, 101, 115, 117, 108, 116, 115, 34, 44, 34, 99, 111, 110, 116, 101, 110, 116,
          84, 121, 112, 101, 34, 58, 34, 97, 112, 112, 108, 105, 99, 97, 116, 105, 111, 110, 47,
          103, 122, 105, 112, 34, 44, 34, 99, 114, 99, 51, 50, 99, 34, 58, 34, 81, 65, 90, 51, 56,
          119, 92, 117, 48, 48, 51, 100, 92, 117, 48, 48, 51, 100, 34, 44, 34, 101, 116, 97, 103,
          34, 58, 34, 67, 78, 102, 88, 114, 77, 84, 65, 54, 118, 69, 67, 69, 65, 69, 92, 117, 48,
          48, 51, 100, 34, 44, 34, 103, 101, 110, 101, 114, 97, 116, 105, 111, 110, 34, 58, 34, 49,
          54, 50, 54, 53, 51, 56, 54, 49, 56, 48, 55, 50, 48, 50, 51, 34, 44, 34, 105, 100, 34, 58,
          34, 116, 101, 114, 114, 97, 45, 107, 101, 114, 110, 101, 108, 45, 107, 56, 115, 45, 116,
          101, 115, 116, 114, 117, 110, 110, 101, 114, 45, 114, 101, 115, 117, 108, 116, 115, 47,
          49, 49, 50, 50, 101, 102, 49, 100, 45, 98, 53, 49, 99, 45, 52, 48, 102, 49, 45, 56, 100,
          54, 54, 45, 56, 99, 52, 97, 52, 101, 102, 50, 48, 50, 50, 48, 46, 116, 97, 114, 46, 103,
          122, 47, 49, 54, 50, 54, 53, 51, 56, 54, 49, 56, 48, 55, 50, 48, 50, 51, 34, 44, 34, 107,
          105, 110, 100, 34, 58, 34, 115, 116, 111, 114, 97, 103, 101, 35, 111, 98, 106, 101, 99,
          116, 34, 44, 34, 109, 100, 53, 72, 97, 115, 104, 34, 58, 34, 67, 54, 66, 117, 55, 56, 71,
          73, 114, 83, 97, 114, 88, 51, 79, 49, 86, 82, 120, 104, 97, 119, 92, 117, 48, 48, 51, 100,
          92, 117, 48, 48, 51, 100, 34, 44, 34, 109, 101, 100, 105, 97, 76, 105, 110, 107, 34, 58,
          34, 104, 116, 116, 112, 115, 58, 47, 47, 119, 119, 119, 46, 103, 111, 111, 103, 108, 101,
          97, 112, 105, 115, 46, 99, 111, 109, 47, 100, 111, 119, 110, 108, 111, 97, 100, 47, 115,
          116, 111, 114, 97, 103, 101, 47, 118, 49, 47, 98, 47, 116, 101, 114, 114, 97, 45, 107,
          101, 114, 110, 101, 108, 45, 107, 56, 115, 45, 116, 101, 115, 116, 114, 117, 110, 110,
          101, 114, 45, 114, 101, 115, 117, 108, 116, 115, 47, 111, 47, 49, 49, 50, 50, 101, 102,
          49, 100, 45, 98, 53, 49, 99, 45, 52, 48, 102, 49, 45, 56, 100, 54, 54, 45, 56, 99, 52, 97,
          52, 101, 102, 50, 48, 50, 50, 48, 46, 116, 97, 114, 46, 103, 122, 63, 103, 101, 110, 101,
          114, 97, 116, 105, 111, 110, 92, 117, 48, 48, 51, 100, 49, 54, 50, 54, 53, 51, 56, 54, 49,
          56, 48, 55, 50, 48, 50, 51, 92, 117, 48, 48, 50, 54, 97, 108, 116, 92, 117, 48, 48, 51,
          100, 109, 101, 100, 105, 97, 34, 44, 34, 109, 101, 116, 97, 103, 101, 110, 101, 114, 97,
          116, 105, 111, 110, 34, 58, 34, 49, 34, 44, 34, 110, 97, 109, 101, 34, 58, 34, 49, 49, 50,
          50, 101, 102, 49, 100, 45, 98, 53, 49, 99, 45, 52, 48, 102, 49, 45, 56, 100, 54, 54, 45,
          56, 99, 52, 97, 52, 101, 102, 50, 48, 50, 50, 48, 46, 116, 97, 114, 46, 103, 122, 34, 44,
          34, 115, 101, 108, 102, 76, 105, 110, 107, 34, 58, 34, 104, 116, 116, 112, 115, 58, 47,
          47, 119, 119, 119, 46, 103, 111, 111, 103, 108, 101, 97, 112, 105, 115, 46, 99, 111, 109,
          47, 115, 116, 111, 114, 97, 103, 101, 47, 118, 49, 47, 98, 47, 116, 101, 114, 114, 97, 45,
          107, 101, 114, 110, 101, 108, 45, 107, 56, 115, 45, 116, 101, 115, 116, 114, 117, 110,
          110, 101, 114, 45, 114, 101, 115, 117, 108, 116, 115, 47, 111, 47, 49, 49, 50, 50, 101,
          102, 49, 100, 45, 98, 53, 49, 99, 45, 52, 48, 102, 49, 45, 56, 100, 54, 54, 45, 56, 99,
          52, 97, 52, 101, 102, 50, 48, 50, 50, 48, 46, 116, 97, 114, 46, 103, 122, 34, 44, 34, 115,
          105, 122, 101, 34, 58, 34, 50, 49, 50, 50, 34, 44, 34, 115, 116, 111, 114, 97, 103, 101,
          67, 108, 97, 115, 115, 34, 58, 34, 83, 84, 65, 78, 68, 65, 82, 68, 34, 44, 34, 116, 105,
          109, 101, 67, 114, 101, 97, 116, 101, 100, 34, 58, 34, 50, 48, 50, 49, 45, 48, 55, 45, 49,
          55, 84, 49, 54, 58, 49, 54, 58, 53, 56, 46, 49, 52, 55, 90, 34, 44, 34, 116, 105, 109,
          101, 83, 116, 111, 114, 97, 103, 101, 67, 108, 97, 115, 115, 85, 112, 100, 97, 116, 101,
          100, 34, 58, 34, 50, 48, 50, 49, 45, 48, 55, 45, 49, 55, 84, 49, 54, 58, 49, 54, 58, 53,
          56, 46, 49, 52, 55, 90, 34, 44, 34, 117, 112, 100, 97, 116, 101, 100, 34, 58, 34, 50, 48,
          50, 49, 45, 48, 55, 45, 49, 55, 84, 49, 54, 58, 49, 54, 58, 53, 56, 46, 49, 52, 55, 90,
          34, 125
        };
  }

  public void assertStorageObjectData(StorageObjectData data) {
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
    System.out.println("Verifying " + filename + " filesize.");
    if (filename.contains("RENDERED")) {
      assertEquals(3584, bytes);
    } else if (filename.contains("RAWDATA")) {
      assertEquals(4579, bytes);
    } else if (filename.contains("SUMMARY")) {
      assertEquals(1350, bytes);
    }
  }

  public class CFContext implements Context {

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

  public class StorageObjectEventHarnessImpl extends StorageObjectEventHarness {

    @Override
    public void doAccept() throws Exception {}
  }
}
