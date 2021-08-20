package bio.terra.cloudfunctions.common;

import com.google.cloud.functions.BackgroundFunction;
import com.google.cloud.functions.Context;
import com.google.gson.JsonSyntaxException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is an abstract representation of a Function triggered by Google Cloud Events. The
 * function casts the Google Cloud Event into a Map of key-value attributes.
 *
 * <p>This class contains only framework-specific logic to receive and parse Google Cloud Event
 * data.
 *
 * <p>Sub-classes of this class can be integrated with a DI framework to deploy Cloud Functions like
 * a Service.
 */
public abstract class GoogleCloudEventHarness implements BackgroundFunction<Map<?, ?>> {
  public static final String GOOGLE_STORAGE_OBJECT_FINALIZE = "google.storage.object.finalize";
  private static final Logger logger = Logger.getLogger(GoogleCloudEventHarness.class.getName());

  private Context context;
  private Map<?, ?> event;

  /**
   * @param event - a key-value map of Google Cloud Event attributes
   * @param context - Google Event Context
   * @throws Exception - when something goes wrong
   */
  @Override
  public void accept(Map<?, ?> event, Context context) throws Exception {
    this.event = event;
    this.context = context;
    validateCloudEvent();
    doAccept();
  }

  private void validateCloudEvent() throws Exception {
    if (!isGoogleStorageObjectFinalize()) {
      logger.log(Level.SEVERE, "Cloud Event Type '" + context.eventType() + "' is not supported.");
      throw new UnSupportedCloudEventTypeException("Unexpected event type: " + context.eventType());
    }
  }

  /** @return - true if event type is google.storage.object.finalize, otherwise false */
  public boolean isGoogleStorageObjectFinalize() {
    return GOOGLE_STORAGE_OBJECT_FINALIZE.equals(getContext().eventType());
  }

  /** @return - Google Cloud Storage Event Context */
  public Context getContext() {
    return context;
  }

  /**
   * @param classOfT - generic type of Google Cloud Event pojo
   * @param <T> - Google Cloud Event pojo
   * @return - an Google Cloud Event pojo of generic type T matching the provider's specification of
   *     the event (e.g. Google Cloud StorageObjectData, MessagePublishedData event data etc.).
   *     Other providers have equivalent events such as AWS S3Event and Azure BlobTrigger. Different
   *     providers use slightly different naming and syntax for the event trigger (e.g. 'accept' for
   *     GC events, 'handleRequest' for S3, 'run' for Azure Blob * Storage)
   * @throws JsonSyntaxException when something goes wrong
   */
  public <T> T getEvent(Class<T> classOfT) throws JsonSyntaxException {
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
