package bio.terra.cloudfunctions.common;

import bio.terra.cloudfunctions.utils.MediaTypeUtils;
import com.google.events.cloud.storage.v1.StorageObjectData;
import java.io.BufferedInputStream;
import java.io.InputStream;

public class FileTypeDetector {
  protected StorageObjectData storageObjectData;
  protected InputStream inputStream;
  protected InputStream dataStream;
  protected MediaTypeWrapper mediaType;

  public FileTypeDetector() {}

  public FileTypeDetector(StorageObjectData storageObjectData) {
    this.storageObjectData = storageObjectData;
  }

  public MediaTypeWrapper getMediaType() {
    if (mediaType == null) mediaType = new MediaTypeWrapper(storageObjectData.getContentType());
    return mediaType;
  }

  public InputStream getDataStream() {
    return dataStream;
  }

  public void setInputStream(InputStream inputStream) {
    this.inputStream = inputStream;
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
              projectId, storageObjectData.getBucket(), storageObjectData.getName());
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
