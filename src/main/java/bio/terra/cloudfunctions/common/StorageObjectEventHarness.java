package bio.terra.cloudfunctions.common;

import bio.terra.cloudfunctions.utils.GsonConverter;
import com.google.cloud.functions.Context;
import com.google.cloud.functions.RawBackgroundFunction;
import com.google.events.cloud.storage.v1.StorageObjectData;
import java.util.logging.Logger;

/**
 * This class is an abstract representation of a Cloud Function triggered by Google Storage Object
 * Event.
 *
 * <p>This class contains only framework-specific logic to receive and parse Google Storage Object
 * Event messages.
 *
 * <p>Sub-classes of this class can be integrated with a DI framework to deploy Cloud Functions like
 * a Service.
 */
public abstract class StorageObjectEventHarness implements RawBackgroundFunction {
  private static final Logger logger = Logger.getLogger(StorageObjectEventHarness.class.getName());

  private Context context;
  private StorageObjectData message;

  /**
   * This method is triggered whenever a target CloudEvent occurs in GCP.
   *
   * @param json event string
   * @throws Exception
   */
  @Override
  public void accept(String json, Context context) throws Exception {
    message = GsonConverter.convertFromClass(json, StorageObjectData.class);
    this.context = context;
    doAccept();
  }

  public Context getContext() {
    return context;
  }

  public StorageObjectData getMessage() {
    return message;
  }
  /**
   * Subclasses of this class must implement this method to kick off application-specific functions
   * downstream .
   *
   * @throws Exception
   */
  public abstract void doAccept() throws Exception;
}
