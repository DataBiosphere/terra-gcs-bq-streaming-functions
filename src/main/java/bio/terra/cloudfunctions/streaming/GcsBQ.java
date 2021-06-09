package bio.terra.cloudfunctions.streaming;

import bio.terra.cloudevents.GCSEvent;
import com.google.cloud.ReadChannel;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.Field;
import com.google.cloud.bigquery.FormatOptions;
import com.google.cloud.bigquery.JobInfo;
import com.google.cloud.bigquery.Schema;
import com.google.cloud.bigquery.StandardSQLTypeName;
import com.google.cloud.bigquery.TableDataWriteChannel;
import com.google.cloud.bigquery.TableId;
import com.google.cloud.bigquery.WriteChannelConfiguration;
import com.google.cloud.bigquery.testing.RemoteBigQueryHelper;
import com.google.cloud.functions.BackgroundFunction;
import com.google.cloud.functions.Context;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.StorageOptions;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.util.Map;
import java.util.logging.Logger;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

public class GcsBQ implements BackgroundFunction<GCSEvent> {
  private static final Logger logger = Logger.getLogger(GcsBQ.class.getName());
  private static final String DATASET = "simple_stream_dataset";
  private static final String TABLE = "us_states_streamtable";
  private static final Schema schema =
      Schema.of(
          Field.newBuilder("id", StandardSQLTypeName.STRING).setMode(Field.Mode.REQUIRED).build(),
          Field.newBuilder("first_name", StandardSQLTypeName.STRING)
              .setMode(Field.Mode.REQUIRED)
              .build(),
          Field.newBuilder("last_name", StandardSQLTypeName.STRING)
              .setMode(Field.Mode.REQUIRED)
              .build());
  private static final Schema schema1 =
      Schema.of(
          Field.of("name", StandardSQLTypeName.STRING),
          Field.of("post_abbr", StandardSQLTypeName.STRING));

  /**
   * Cloud Function Event Handler
   *
   * @param event Native Google Storage PUB/SUB Event
   * @param context Gloud Function Context
   * @throws Exception
   */
  @Override
  public void accept(GCSEvent event, Context context) throws Exception {
    logger.info("Event: " + context.eventId());
    logger.info("Event Type: " + context.eventType());
    logger.info(event.toString());

    for (Map.Entry<String, String> entry : System.getenv().entrySet())
      logger.info("Key = " + entry.getKey() + ", Value = " + entry.getValue());

    String projectId = System.getenv("GCLOUD_PROJECT");
    String bucketName = event.getBucket();
    String objectName = event.getName();

    InputStream in = getObjectAsInputStream(projectId, bucketName, objectName);

    // Uncompress .gz as CompressorInputStream
    CompressorStreamFactory compressor = CompressorStreamFactory.getSingleton();
    CompressorInputStream uncompressedInputStream =
        in.markSupported()
            ? compressor.createCompressorInputStream(in)
            : compressor.createCompressorInputStream(new BufferedInputStream(in));

    // Untar .tar as ArchiveInputStream
    ArchiveStreamFactory archiver = new ArchiveStreamFactory();
    ArchiveInputStream archiveInputStream =
        uncompressedInputStream.markSupported()
            ? archiver.createArchiveInputStream(uncompressedInputStream)
            : archiver.createArchiveInputStream(new BufferedInputStream(uncompressedInputStream));

    // ArchiveInputStream is a special type of InputStream that emits an EOF when it gets to the end
    // of a file in the archive.
    // Once it’s done, call getNextEntry to reset the stream and start reading the next file.
    // When getNextEntry returns null, you’re at the end of the archive.
    ArchiveEntry archiveEntry;
    while ((archiveEntry = archiveInputStream.getNextEntry()) != null) {
      logger.info(archiveEntry.getName() + " " + archiveEntry.getSize() + " bytes");
      if (archiveEntry.getName().contains("SUMMARY_testRun.json")) {
        logger.info("Processing " + archiveEntry.getName());
        byte[] json = readEntry(archiveInputStream, archiveEntry.getSize());
        logger.info(new String(json));
        streamToBQ(projectId, DATASET, TABLE, json);
      }
    }
  }

  // Open a channel to stream json string to BigQuery
  private void streamToBQ(String projectId, String dataset, String table, byte[] json)
      throws IOException {
    TableId tableId = TableId.of(projectId, dataset, table);
    logger.info(tableId.getProject() + ":" + tableId.getDataset() + ":" + tableId.getTable());
    WriteChannelConfiguration configuration =
        WriteChannelConfiguration.newBuilder(tableId)
            .setFormatOptions(FormatOptions.json())
            .setCreateDisposition(JobInfo.CreateDisposition.CREATE_IF_NEEDED)
            .setSchema(schema1)
            .build();
    BigQuery bigquery = RemoteBigQueryHelper.create().getOptions().getService();
    TableDataWriteChannel channel = bigquery.writer(configuration);
    logger.info(channel.toString());
    try {
      channel.write(ByteBuffer.wrap(json));
    } catch (Exception e) {
      logger.info(e.getMessage());
    } finally {
      channel.close();
    }
  }

  /**
   * If a Service Account has not been specified for Google Cloud Function deployment, then the
   * Cloud Function will assume the roles of the default IAM Service Account
   * PROJECT_ID@appspot.gserviceaccount.com at runtime.
   *
   * <p>The Service Account for any Java 11 runtime (Compute Engine, App Engine, or GKE) must have
   * appropriate GCS read permissions.
   *
   * <p>Opens an input stream to GCS Object.
   *
   * @param projectId the Google Project ID
   * @param bucketName the Google Storage Bucket
   * @param objectName the Google Storage Bucket File Object
   * @return URL object
   */
  private InputStream getObjectAsInputStream(String projectId, String bucketName, String objectName)
      throws StorageException {
    Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
    ReadChannel reader = storage.reader(bucketName, objectName);
    return Channels.newInputStream(reader);
  }

  private byte[] readEntry(InputStream input, final long size) throws IOException {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    int bufferSize = 1024;
    byte[] buffer = new byte[bufferSize + 1];
    long remaining = size;
    while (remaining > 0) {
      int len = (int) Math.min(remaining, bufferSize);
      int read = input.read(buffer, 0, len);
      remaining -= read;
      output.write(buffer, 0, read);
    }
    return output.toByteArray();
  }
}
