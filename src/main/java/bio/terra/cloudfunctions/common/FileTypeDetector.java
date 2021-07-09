package bio.terra.cloudfunctions.common;

import bio.terra.cloudfunctions.utils.MediaTypeUtils;
import com.google.cloud.functions.Context;
import com.google.cloud.functions.RawBackgroundFunction;
import com.google.events.cloud.storage.v1.StorageObjectData;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;

public abstract class FileTypeDetector<E extends StorageObjectData>
    implements RawBackgroundFunction {
  protected E event;
  protected Context eventContext;
  protected InputStream inputStream;
  protected InputStream dataStream;
  protected MediaTypeWrapper mediaType;

  public FileTypeDetector() {}

  public E getEvent() {
    return event;
  }

  public Context getEventContext() {
    return eventContext;
  }

  public InputStream getDataStream() {
    return dataStream;
  }

  public void setInputStream(InputStream inputStream) {
    this.inputStream = inputStream;
  }
  /**
   * Override the base method in RawBackgroundFunction.
   *
   * <p>This is the endpoint triggered by the GCS event. It captures the content type of the object
   * triggering this method and delegate the file type handling to the handleMediaType() method.
   *
   * @param json the String representation of the GCS event
   * @param context the Cloud Function context
   */
  @Override
  public final void accept(String json, Context context) throws Exception {
    this.eventContext = context;
    ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
    @SuppressWarnings("unchecked")
    Class<E> eClass = (Class<E>) genericSuperclass.getActualTypeArguments()[0];
    this.event = EventWrapper.parseEvent(json, eClass);
    this.mediaType = new MediaTypeWrapper(this.event.getContentType());
    handleMediaType();
  }
  /**
   * Obtain an input stream from the GCS object and convert it to a typed input stream. It supports
   * GZIP and JSON file types. For GZIP, the method returns the ArchiveInputStream or
   * BufferedInputStream to dataStream property, depending on whether the GZIp is a TAR archive or
   * not. If the file type is JSON, then BufferedInputStream will be returned to dataStream.
   */
  public void handleMediaType() throws Exception {
    String projectId = System.getenv("GCLOUD_PROJECT");
    if (inputStream == null)
      inputStream =
          MediaTypeUtils.getStorageObjectDataAsInputStream(
              projectId, this.event.getBucket(), this.event.getName());
    if (this.mediaType.isApplicationGzip()) {
      dataStream = handleGzipType(inputStream);
    } else if (this.mediaType.isApplicationJson()) {
      dataStream = handleJsonType(inputStream);
    }
  }

  private InputStream handleGzipType(InputStream in) {
    BufferedInputStream bis = null;
    try {
      bis = new BufferedInputStream(MediaTypeUtils.createCompressorInputStream(in));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    if (bis != null) {
      try {
        return MediaTypeUtils.createArchiveInputStream(bis);
      } catch (Exception e) {
        return bis;
      }
    }
    return null;
  }

  private InputStream handleJsonType(InputStream in) {
    try {
      return new BufferedInputStream(in);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
