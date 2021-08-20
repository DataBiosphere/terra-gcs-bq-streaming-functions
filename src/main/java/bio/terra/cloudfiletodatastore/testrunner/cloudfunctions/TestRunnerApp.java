package bio.terra.cloudfiletodatastore.testrunner.cloudfunctions;

import bio.terra.cloudfiletodatastore.FileUploadedMessage;
import bio.terra.cloudfunctions.common.App;
import bio.terra.cloudfunctions.utils.BigQueryUtils;
import bio.terra.cloudfunctions.utils.GcsUtils;
import bio.terra.cloudfunctions.utils.MediaTypeUtils;
import com.google.common.io.Files;
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
    String projectId = System.getenv("GCLOUD_PROJECT");
    String dataSet = System.getenv("BQ_DATASET");
    String table = System.getenv("BQ_TABLE");

    InputStream in =
        GcsUtils.getStorageObjectDataAsInputStream(projectId, sourceBucket, resourceName);

    CompressorInputStream compressedInputStream = MediaTypeUtils.createCompressorInputStream(in);

    ArchiveInputStream archiveInputStream =
        MediaTypeUtils.createArchiveInputStream(compressedInputStream);

    /*
     * ArchiveInputStream is a special type of InputStream that emits an EOF when it gets to the end
     * of a file in the archive. Once it’s done, call getNextEntry to reset the stream and start
     * reading the next file. When getNextEntry returns null, you’re at the end of the archive.
     */
    ArchiveEntry archiveEntry;
    while ((archiveEntry = archiveInputStream.getNextEntry()) != null) {
      if (!archiveEntry.isDirectory()) {
        if (Files.getNameWithoutExtension(archiveEntry.getName()).equals(table)) {
          logger.info(
              String.format(
                  "Processing %s (%s bytes) for streaming to BQ table %s",
                  archiveEntry.getName(),
                  archiveEntry.getSize(),
                  Files.getNameWithoutExtension(archiveEntry.getName())));
          byte[] data = MediaTypeUtils.readEntry(archiveInputStream, archiveEntry.getSize());
          BigQueryUtils.streamToBQ(projectId, dataSet, table, data);
        }
      }
    }
  }
}
