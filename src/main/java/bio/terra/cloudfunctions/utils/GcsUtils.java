package bio.terra.cloudfunctions.utils;

import com.google.cloud.ReadChannel;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.StorageOptions;
import java.io.InputStream;
import java.nio.channels.Channels;

public class GcsUtils {
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
}
