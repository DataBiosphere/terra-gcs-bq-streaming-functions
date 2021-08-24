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

  public TestRunnerStreamingProcessor(FileUploadedMessage fileUploadedMessage) {
    super(fileUploadedMessage);
  }

  @Override
  public void processMessage() {
    String sourceBucket = message.getSourceBucket();
    String resourceName = message.getResourceName();
    String projectId = System.getenv("GCLOUD_PROJECT");
    String dataSet = System.getenv("BQ_DATASET");
    String table = System.getenv("BQ_TABLE");

    InputStream in =
        GcsUtils.getStorageObjectDataAsInputStream(projectId, sourceBucket, resourceName);

    // The intermediate step is necessary for ArchiveInputStream to recognize that the file being
    // processed is a tar.gz
    try (ArchiveInputStream archiveInputStream =
        MediaTypeUtils.createArchiveInputStream(MediaTypeUtils.createCompressorInputStream(in))) {
      /*
       * ArchiveInputStream is a special type of InputStream that emits an EOF when it gets to the end
       * of a file in the archive. Once it’s done, call getNextEntry to reset the stream and start
       * reading the next file. When getNextEntry returns null, you’re at the end of the archive.
       */
      ArchiveEntry archiveEntry;
      while ((archiveEntry = archiveInputStream.getNextEntry()) != null) {
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
        BigQueryUtils.streamToBQ(projectId, dataSet, table, data);
      }
    } catch (ArchiveException | IOException | CompressorException e) {
      logger.log(Level.SEVERE, e.getMessage());
    }
  }
}
