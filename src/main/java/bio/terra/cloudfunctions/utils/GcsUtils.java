package bio.terra.cloudfunctions.utils;

import com.google.cloud.ReadChannel;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.StorageOptions;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GcsUtils {
  private static final Logger logger = Logger.getLogger(GcsUtils.class.getName());
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
   * @param projectId - Google Project ID
   * @param bucket - Google Storage Bucket
   * @param objectName - Google Storage File Object
   * @return InputStream - constructs an InputStream to the file object
   * @throws RuntimeException - if the file object content cannot be opened
   */
  public static InputStream getStorageObjectDataAsInputStream(
      String projectId, String bucket, String objectName) {
    try {
      Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
      ReadChannel reader = storage.reader(bucket, objectName);
      return Channels.newInputStream(reader);
    } catch (StorageException e) {
      logger.log(Level.SEVERE, "StorageException: " + e.getMessage());
      throw new RuntimeException(e);
    }
  }
}
