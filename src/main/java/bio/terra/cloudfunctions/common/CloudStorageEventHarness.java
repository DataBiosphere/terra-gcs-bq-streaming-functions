package bio.terra.cloudfunctions.common;

import bio.terra.cloudevents.GCSEvent;
import com.google.cloud.functions.BackgroundFunction;
import com.google.cloud.functions.Context;
import com.google.gson.internal.LinkedTreeMap;
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
    GCSEvent gcsEvent =
        GsonWrapper.convertFromClass(GsonWrapper.getInstance().toJson(this.event), GCSEvent.class);
    logger.info(
        "CloudStorageEventHarness: "
            + gcsEvent.getBucket()
            + " "
            + gcsEvent.getContentType()
            + " "
            + context.eventType());
    doAccept();
  }

  public Context getContext() {
    return context;
  }

  public LinkedTreeMap<?, ?> getEvent() {
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
