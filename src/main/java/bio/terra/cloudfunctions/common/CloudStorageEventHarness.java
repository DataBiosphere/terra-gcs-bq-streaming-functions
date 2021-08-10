package bio.terra.cloudfunctions.common;

import com.google.cloud.functions.BackgroundFunction;
import com.google.cloud.functions.Context;
import java.util.logging.Logger;

/**
 * This class is an abstract representation of a Function triggered by a Cloud Storage Event. It
 * accepts a generic event type T which is a proxy of the provider's specification of the event.
 * (e.g. GCSEvent is a proxy of GCS StorageObjectData event). Other providers have equivalent events
 * such as AWS S3Event and Azure BlobTrigger. Different providers use slightly different naming and
 * syntax for the event trigger (e.g. accept for GCS, handleRequest for S3, run for Azure Blob
 * Storage)
 *
 * <p>This class contains only framework-specific logic to receive and parse Google Storage Object
 * Event messages.
 *
 * <p>Sub-classes of this class can be integrated with a DI framework to deploy Cloud Functions like
 * a Service.
 */
public abstract class CloudStorageEventHarness<T> implements BackgroundFunction<T> {
  private static final Logger logger = Logger.getLogger(CloudStorageEventHarness.class.getName());

  private Context context;
  private T event;

  /**
   *
   * @param event String
   * @param context event function context
   * @throws Exception when something goes wrong
   */
  @Override
  public void accept(T event, Context context) throws Exception {
    this.event = event;
    this.context = context;
    doAccept();
  }

  public Context getContext() {
    return context;
  }

  public T getEvent() {
    return event;
  }

  /**
   * Subclasses of this class must implement this method to kick off application-specific functions
   * downstream .
   *
   * @throws Exception when something wrong happened
   */
  public abstract void doAccept() throws Exception;
}
