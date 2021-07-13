package bio.terra.cloudfunctions.utils;

import com.google.cloud.ReadChannel;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.StorageOptions;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

public class MediaTypeUtils {
  /**
   * If a Service Account has not been specified for Google Cloud Function deployment, then the
   * Cloud Function will assume the roles of the default IAM Service Account
   * PROJECT_ID@appspot.gserviceaccount.com at runtime.
   *
   * <p>The Service Account for any Java 11 runtime (Compute Engine, App Engine, or GKE) must have
   * appropriate GCS read permissions.
   *
   * <p>Opens an input stream to GCS Object.
   *
   * @param projectId the Google Project ID
   * @param bucket the Google Storage Bucket
   * @param objectName the Google Storage Bucket File Object
   * @return URL object
   */
  public static InputStream getStorageObjectDataAsInputStream(
      String projectId, String bucket, String objectName) throws StorageException {
    Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
    ReadChannel reader = storage.reader(bucket, objectName);
    return Channels.newInputStream(reader);
  }
  /**
   * Create an compressor input stream from an input stream, autodetecting the compressor type from
   * the first few bytes of the stream. The InputStream must support marks, like
   * BufferedInputStream.
   *
   * @param in the InputStream
   * @return CompressorInputStream object
   */
  public static CompressorInputStream createCompressorInputStream(InputStream in)
      throws CompressorException {
    CompressorStreamFactory compressor = CompressorStreamFactory.getSingleton();
    return in.markSupported()
        ? compressor.createCompressorInputStream(in)
        : compressor.createCompressorInputStream(new BufferedInputStream(in));
  }
  /**
   * Create an archive input stream from an input stream, autodetecting the archive type from the
   * first few bytes of the stream. The InputStream must support marks, like BufferedInputStream.
   *
   * @param in the InputStream
   * @return ArchiveInputStream object
   */
  public static ArchiveInputStream createArchiveInputStream(InputStream in)
      throws ArchiveException {
    ArchiveStreamFactory archiver = new ArchiveStreamFactory();
    return in.markSupported()
        ? archiver.createArchiveInputStream(in)
        : archiver.createArchiveInputStream(new BufferedInputStream(in));
  }
  /**
   * Read up to a certain number of bytes given by the size parameter from the input stream.
   *
   * @param in the InputStream
   * @param size number of bytes to read from the input stream
   * @return byte[] read
   */
  public static byte[] readEntry(InputStream in, final long size) throws IOException {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    int bufferSize = 1024;
    byte[] buffer = new byte[bufferSize + 1];
    long remaining = size;
    while (remaining > 0) {
      int len = (int) Math.min(remaining, bufferSize);
      int read = in.read(buffer, 0, len);
      remaining -= read;
      output.write(buffer, 0, read);
    }
    return output.toByteArray();
  }
}
