package bio.terra.cloudfunctions.common;

import bio.terra.cloudfunctions.utils.GcsUtils;
import bio.terra.cloudfunctions.utils.MediaTypeUtils;
import com.google.events.cloud.storage.v1.StorageObjectData;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.logging.Logger;

/**
 * This class detects the raw file type from the StorageObjectData and opens an appropriate
 * InputStream to the underlying Google storage object.
 *
 * <p>Sub-classes of this class can be integrated with a DI framework to deploy Cloud Functions like
 * a Service.
 */
public class FileTypeDetector {
    private static final Logger logger = Logger.getLogger(FileTypeDetector.class.getName());
    private StorageObjectData storageObjectData;
    protected InputStream inputStream;
    protected InputStream dataStream;

    public FileTypeDetector(StorageObjectData storageObjectData) {
        this.storageObjectData = storageObjectData;
    }

    private MediaTypeWrapper getMediaType() {
        return new MediaTypeWrapper(storageObjectData.getContentType());
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
                    GcsUtils.getStorageObjectDataAsInputStream(
                            projectId, storageObjectData.getBucket(), storageObjectData.getName());
        if (getMediaType().isApplicationGzip()) {
            dataStream = handleGzipType(inputStream);
        } else if (getMediaType().isApplicationJson()) {
            dataStream = handleJsonType(inputStream);
        }
    }

    private InputStream handleGzipType(InputStream in) {
        BufferedInputStream bis;
        logger.info("Invoke Gzip handler...");
        try {
            bis = new BufferedInputStream(MediaTypeUtils.createCompressorInputStream(in));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            return MediaTypeUtils.createArchiveInputStream(bis);
        } catch (Exception e) {
            return bis;
        }
    }

    private InputStream handleJsonType(InputStream in) {
        logger.info("Invoke Json handler...");
        try {
            return new BufferedInputStream(in);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}