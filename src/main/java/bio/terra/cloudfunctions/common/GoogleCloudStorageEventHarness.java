package bio.terra.cloudfunctions.common;

import com.google.cloud.functions.BackgroundFunction;
import com.google.cloud.functions.Context;
import com.google.gson.JsonSyntaxException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is an abstract representation of a Function triggered by a Google Cloud Storage Event.
 * The function casts the Google Cloud Storage Event into a Map of key-value pairs.
 *
 * <p>This class contains only framework-specific logic to receive and parse Google Storage Object
 * Event messages.
 *
 * <p>Sub-classes of this class can be integrated with a DI framework to deploy Cloud Functions like
 * a Service.
 */
public abstract class GoogleCloudStorageEventHarness implements BackgroundFunction<Map<?, ?>> {
  private static final Logger logger =
      Logger.getLogger(GoogleCloudStorageEventHarness.class.getName());

  private Context context;
  private Map<?, ?> event;

  /**
   * @param event String
   * @param context event function context
   * @throws Exception when something goes wrong
   */
  @Override
  public void accept(Map<?, ?> event, Context context) throws Exception {
    this.event = event;
    this.context = context;
    doAccept();
  }

  /** @return Google Cloud Storage Event Context */
  public Context getContext() {
    return context;
  }

  /**
   * @param classOfT generic type of event pojo
   * @param <T> generic event pojo
   * @return an event pojo of generic type T matching the provider's specification of the event. *
   *     (e.g. GCSEvent is a pojo of GCS StorageObjectData event). Other providers have equivalent
   *     events * such as AWS S3Event and Azure BlobTrigger. Different providers use slightly
   *     different naming and * syntax for the event trigger (e.g. accept for GCS, handleRequest for
   *     S3, run for Azure Blob * Storage)
   * @throws Exception when something goes wrong
   */
  public <T> T getEvent(Class<T> classOfT) {
    try {
      return GsonWrapper.convertFromClass(this.event, classOfT);
    } catch (JsonSyntaxException e) {
      logger.log(Level.SEVERE, "Could not convert from JSON", e);
      throw e;
    }
  }

  /**
   * Subclasses of this class must implement this method to kick off application-specific functions
   * downstream.
   *
   * @throws Exception when something wrong happened
   */
  public abstract void doAccept() throws Exception;
}
