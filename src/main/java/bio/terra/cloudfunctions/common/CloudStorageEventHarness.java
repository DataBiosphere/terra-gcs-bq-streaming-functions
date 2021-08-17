package bio.terra.cloudfunctions.common;

import com.google.cloud.functions.BackgroundFunction;
import com.google.cloud.functions.Context;
import com.google.gson.internal.LinkedTreeMap;
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
public abstract class CloudStorageEventHarness implements BackgroundFunction<LinkedTreeMap<?, ?>> {
  private static final Logger logger = Logger.getLogger(CloudStorageEventHarness.class.getName());

  private Context context;
  private LinkedTreeMap<?, ?> event;

  /**
   * @param event String
   * @param context event function context
   * @throws Exception when something goes wrong
   */
  @Override
  public void accept(LinkedTreeMap<?, ?> event, Context context) throws Exception {
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
   * @param <T>
   * @return an event pojo of generic type T matching the provider's specification of the event. *
   *     (e.g. GCSEvent is a pojo of GCS StorageObjectData event). Other providers have equivalent
   *     events * such as AWS S3Event and Azure BlobTrigger. Different providers use slightly
   *     different naming and * syntax for the event trigger (e.g. accept for GCS, handleRequest for
   *     S3, run for Azure Blob * Storage)
   * @throws Exception
   */
  public <T> T getEvent(Class<T> classOfT) throws Exception {
    try {
      return GsonWrapper.convertFromClass(GsonWrapper.getInstance().toJson(this.event), classOfT);
    } catch (Exception e) {
      logger.severe(e.getMessage());
      throw e;
    }
  }

  /**
   * Subclasses of this class must implement this method to kick off application-specific functions
   * downstream .
   *
   * @throws Exception when something wrong happened
   */
  public abstract void doAccept() throws Exception;
}
