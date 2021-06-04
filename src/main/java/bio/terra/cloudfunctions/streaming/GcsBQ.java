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
    generateV4GetObjectSignedUrl(projectId, bucketName, objectName);

    InputStream in = new URL(event.getMediaLink()).openStream();
    CompressorStreamFactory compressor = CompressorStreamFactory.getSingleton();
    CompressorInputStream uncompressedInputStream =
        in.markSupported()
            ? compressor.createCompressorInputStream(in)
            : compressor.createCompressorInputStream(new BufferedInputStream(in));

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
   */
  private void generateV4GetObjectSignedUrl(String projectId, String bucketName, String objectName)
      throws StorageException {
    Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();

    // Define resource
    BlobInfo blobInfo = BlobInfo.newBuilder(BlobId.of(bucketName, objectName)).build();

    URL url =
        storage.signUrl(blobInfo, 5, TimeUnit.MINUTES, Storage.SignUrlOption.withV4Signature());

    logger.info("Generated GET signed URL:");
    logger.info(url.toString());
    logger.info("You can use this URL with any user agent, for example:");
    logger.info("curl '" + url + "'");
  }
}
