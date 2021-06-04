package bio.terra.cloudfunctions.streaming;

import bio.terra.cloudevents.GCSEvent;
import com.google.cloud.functions.BackgroundFunction;
import com.google.cloud.functions.Context;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.StorageOptions;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

public class GcsBQ implements BackgroundFunction<GCSEvent> {
  private static final Logger logger = Logger.getLogger(GcsBQ.class.getName());
  private static final long SIGNED_URL_DURATION_MINUTES = 5;
  private static volatile URL shortLivedUrl;

  /**
   * Cloud Function Event Handler
   *
   * @param event Native Google Storage PUB/SUB Event
   * @param context Gloud Function Context
   * @throws Exception
   */
  @Override
  public void accept(GCSEvent event, Context context) throws Exception {
    logger.info("Event: " + context.eventId());
    logger.info("Event Type: " + context.eventType());
    logger.info(event.toString());

    for (Map.Entry<String, String> entry : System.getenv().entrySet())
      logger.info("Key = " + entry.getKey() + ", Value = " + entry.getValue());

    String projectId = System.getenv("GCLOUD_PROJECT");
    String bucketName = event.getBucket();
    String objectName = event.getName();
    shortLivedUrl = generateV4GetObjectSignedUrl(projectId, bucketName, objectName);
    logger.info("Short lived URL" + shortLivedUrl);

    InputStream in = shortLivedUrl.openStream();

    // Uncompress .gz as CompressorInputStream
    CompressorStreamFactory compressor = CompressorStreamFactory.getSingleton();
    CompressorInputStream uncompressedInputStream =
        in.markSupported()
            ? compressor.createCompressorInputStream(in)
            : compressor.createCompressorInputStream(new BufferedInputStream(in));

    // Untar .tar as ArchiveInputStream
    ArchiveStreamFactory archiver = new ArchiveStreamFactory();
    ArchiveInputStream archiveInputStream =
        uncompressedInputStream.markSupported()
            ? archiver.createArchiveInputStream(uncompressedInputStream)
            : archiver.createArchiveInputStream(new BufferedInputStream(uncompressedInputStream));

    // ArchiveInputStream is a special type of InputStream that emits an EOF when it gets to the end
    // of a file in the archive.
    // Once it’s done, call getNextEntry to reset the stream and start reading the next file.
    // When getNextEntry returns null, you’re at the end of the archive.
    ArchiveEntry archiveEntry;
    while ((archiveEntry = archiveInputStream.getNextEntry()) != null) {
      logger.info(archiveEntry.getName() + " " + archiveEntry.getSize() + " bytes");
    }
  }

  /**
   * Signing a URL requires Credentials which implement ServiceAccountSigner. These can be set
   * explicitly using the Storage.SignUrlOption.signWith(ServiceAccountSigner) option. If you don't,
   * you could also pass a service account signer to StorageOptions, i.e.
   * StorageOptions().newBuilder().setCredentials(ServiceAccountSignerCredentials). In this example,
   * neither of these options are used, which means the following code only works when the
   * credentials are defined via the environment variable GOOGLE_APPLICATION_CREDENTIALS, and those
   * credentials are authorized to sign a URL. See the documentation for Storage.signUrl for more
   * details.
   *
   * <p>If a Service Account has not been specified for Google Cloud Function deployment, then the
   * Cloud Function will assume the roles of the default IAM Service Account
   * PROJECT_ID@appspot.gserviceaccount.com at runtime.
   *
   * <p>The Service Account for any Java 11 runtime (Compute Engine, App Engine, or GKE) must have
   * iam.serviceAccounts.signBlob permission. This permission is granted by the Service Account
   * Token Creator Role. Please refer to
   *
   * <p>gcloud iam roles describe roles/iam.serviceAccountTokenCreator
   *
   * <p>Generate a short-lived public URL for the Google Storage File Object
   *
   * <p>You can use this URL with any user agent, for example: curl url
   *
   * @param projectId the Google Project ID
   * @param bucketName the Google Storage Bucket
   * @param objectName the Google Storage Bucket File Object
   * @return URL object
   */
  private URL generateV4GetObjectSignedUrl(String projectId, String bucketName, String objectName)
      throws StorageException {
    Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();

    // Define resource to sign
    BlobInfo blobInfo = BlobInfo.newBuilder(BlobId.of(bucketName, objectName)).build();

    return storage.signUrl(
        blobInfo,
        SIGNED_URL_DURATION_MINUTES,
        TimeUnit.MINUTES,
        Storage.SignUrlOption.withV4Signature());
  }
}
