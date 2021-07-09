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
