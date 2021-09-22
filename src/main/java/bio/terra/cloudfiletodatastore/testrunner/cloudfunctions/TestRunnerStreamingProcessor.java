package bio.terra.cloudfiletodatastore.testrunner.cloudfunctions;

import bio.terra.cloudfiletodatastore.FileUploadedMessage;
import bio.terra.cloudfiletodatastore.MessageProcessor;
import bio.terra.cloudfunctions.utils.BigQueryUtils;
import bio.terra.cloudfunctions.utils.GcsUtils;
import bio.terra.cloudfunctions.utils.MediaTypeUtils;
import com.google.common.io.Files;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.compressors.CompressorException;

public class TestRunnerStreamingProcessor extends MessageProcessor {
  private static final Logger logger =
      Logger.getLogger(TestRunnerStreamingProcessor.class.getName());

  protected String projectId;
  protected String dataSet;
  protected String table;

  public TestRunnerStreamingProcessor(FileUploadedMessage fileUploadedMessage) {
    super(fileUploadedMessage);
  }

  @Override
  public void processMessage() {
    String sourceBucket = message.getSourceBucket();
    String resourceName = message.getResourceName();
    logger.log(Level.INFO, resourceName);

    loadEnvVars();

    // The intermediate step is necessary for ArchiveInputStream to recognize that the file being
    // processed is a tar.gz
    try (ArchiveInputStream archiveInputStream =
        MediaTypeUtils.createArchiveInputStream(
            MediaTypeUtils.createCompressorInputStream(
                getStorageObjectDataAsInputStream(projectId, sourceBucket, resourceName)))) {

      logger.log(Level.INFO, "--" + archiveInputStream.available());
      /*
       * ArchiveInputStream is a special type of InputStream that emits an EOF when it gets to the end
       * of a file in the archive. Once it’s done, call getNextEntry to reset the stream and start
       * reading the next file. When getNextEntry returns null, you’re at the end of the archive.
       */
      ArchiveEntry archiveEntry;
      while ((archiveEntry = archiveInputStream.getNextEntry()) != null) {
        logger.log(Level.INFO, "--" + archiveEntry.getName());
        if (archiveEntry.isDirectory()) {
          continue;
        }

        if (!Files.getNameWithoutExtension(archiveEntry.getName()).equals(table)) {
          logger.log(
              Level.WARNING,
              String.format(
                  "Processing skipped for project %s: bucket %s and resource name %s.",
                  projectId, sourceBucket, resourceName));
          continue;
        }
        logger.log(
            Level.INFO,
            String.format(
                "Processing %s (%s bytes) for streaming to BQ table %s",
                archiveEntry.getName(),
                archiveEntry.getSize(),
                Files.getNameWithoutExtension(archiveEntry.getName())));

        byte[] data = archiveInputStream.readAllBytes();
        streamToBQ(projectId, dataSet, table, data);
      }
    } catch (ArchiveException | IOException | CompressorException e) {
      logger.log(Level.SEVERE, e.getMessage());
    }
  }

  public void loadEnvVars() {
    projectId = System.getenv("GCLOUD_PROJECT");
    dataSet = System.getenv("BQ_DATASET");
    table = System.getenv("BQ_TABLE");
    logger.log(Level.INFO, String.format("%s %s %s", projectId, dataSet, table));
  }

  public InputStream getStorageObjectDataAsInputStream(
      String projectId, String sourceBucket, String resourceName) {
    logger.log(Level.INFO, String.format("%s %s %s", projectId, sourceBucket, resourceName));
    return GcsUtils.getStorageObjectDataAsInputStream(projectId, sourceBucket, resourceName);
  }

  public void streamToBQ(String projectId, String dataSet, String table, byte[] data)
      throws IOException {
    BigQueryUtils.streamToBQ(projectId, dataSet, table, data);
  }
}
