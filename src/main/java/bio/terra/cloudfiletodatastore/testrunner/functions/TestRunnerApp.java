package bio.terra.cloudfiletodatastore.testrunner.functions;

import bio.terra.cloudevents.CloudStorageEventType;
import bio.terra.cloudfiletodatastore.FileUploadedMessage;
import bio.terra.cloudfunctions.common.App;
import bio.terra.cloudfunctions.common.MediaTypeWrapper;
import bio.terra.cloudfunctions.utils.BigQueryUtils;
import bio.terra.cloudfunctions.utils.GcsUtils;
import bio.terra.cloudfunctions.utils.MediaTypeUtils;
import java.io.InputStream;
import java.util.logging.Logger;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.compressors.CompressorInputStream;

public class TestRunnerApp extends App {
  private static final Logger logger = Logger.getLogger(TestRunnerApp.class.getName());

  public TestRunnerApp(FileUploadedMessage fileUploadedMessage) {
    super(fileUploadedMessage);
  }

  @Override
  public void process() throws Exception {
    String sourceBucket = fileUploadedMessage.getSourceBucket();
    String resourceName = fileUploadedMessage.getResourceName();
    MediaTypeWrapper mediaType = new MediaTypeWrapper(fileUploadedMessage.getContentType());
    CloudStorageEventType cloudStorageEventType = fileUploadedMessage.getCloudStorageEventType();

    if (CloudStorageEventType.GOOGLE_STORAGE_OBJECT_FINALIZE
        .getCode()
        .equals(cloudStorageEventType.getCode())) {
      if (sourceBucket.toLowerCase().contains("testrunner") && mediaType.isApplicationGzip()) {
        String projectId = System.getenv("GCLOUD_PROJECT");
        String dataSet = System.getenv("DATASET");
        String table = System.getenv("TABLE");

        InputStream in =
            GcsUtils.getStorageObjectDataAsInputStream(projectId, sourceBucket, resourceName);

        CompressorInputStream compressedInputStream =
            MediaTypeUtils.createCompressorInputStream(in);

        ArchiveInputStream archiveInputStream =
            MediaTypeUtils.createArchiveInputStream(compressedInputStream);

        /**
         * ArchiveInputStream is a special type of InputStream that emits an EOF when it gets to the
         * end of a file in the archive. Once it’s done, call getNextEntry to reset the stream and
         * start reading the next file. When getNextEntry returns null, you’re at the end of the
         * archive.
         */
        ArchiveEntry archiveEntry;
        while ((archiveEntry = archiveInputStream.getNextEntry()) != null) {
          if (archiveEntry.getName().contains(table)) {
            logger.info(
                "Processing " + archiveEntry.getName() + " " + archiveEntry.getSize() + " bytes");
            byte[] datajson = MediaTypeUtils.readEntry(archiveInputStream, archiveEntry.getSize());
            BigQueryUtils.streamToBQ(projectId, dataSet, table, datajson);
          }
        }
      }
    }
  }
}
