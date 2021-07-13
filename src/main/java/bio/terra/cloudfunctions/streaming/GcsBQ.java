package bio.terra.cloudfunctions.streaming;

import bio.terra.cloudevents.GCSEvent;
import bio.terra.cloudfunctions.utils.BigQueryUtils;
import bio.terra.cloudfunctions.utils.MediaTypeUtils;
import com.google.cloud.bigquery.Field;
import com.google.cloud.bigquery.LegacySQLTypeName;
import com.google.cloud.bigquery.Schema;
import com.google.cloud.bigquery.StandardSQLTypeName;
import com.google.cloud.functions.BackgroundFunction;
import com.google.cloud.functions.Context;
import java.io.InputStream;
import java.util.Map;
import java.util.logging.Logger;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.compressors.CompressorInputStream;

public class GcsBQ implements BackgroundFunction<GCSEvent> {
  private static final Logger logger = Logger.getLogger(GcsBQ.class.getName());
  // This SCHEMA code is now replaced by the json representation in resources/<module>/schemas.
  // The creation of the table is delegated to the bq cli rather than in code here.
  // BigQuery Java API does not yet support instantiating Schema object from Json.
  // We will keep this SCHEMA code here in case we need it in the future.
  private static final Schema SCHEMA =
      Schema.of(
          Field.newBuilder("id", StandardSQLTypeName.STRING).setMode(Field.Mode.REQUIRED).build(),
          Field.newBuilder("startTime", StandardSQLTypeName.INT64)
              .setMode(Field.Mode.REQUIRED)
              .build(),
          Field.newBuilder("endTime", StandardSQLTypeName.INT64)
              .setMode(Field.Mode.REQUIRED)
              .build(),
          Field.newBuilder("startUserJourneyTime", StandardSQLTypeName.INT64)
              .setMode(Field.Mode.REQUIRED)
              .build(),
          Field.newBuilder("endUserJourneyTime", StandardSQLTypeName.INT64)
              .setMode(Field.Mode.REQUIRED)
              .build(),
          Field.newBuilder("startTimestamp", StandardSQLTypeName.TIMESTAMP)
              .setMode(Field.Mode.REQUIRED)
              .build(),
          Field.newBuilder("endTimestamp", StandardSQLTypeName.TIMESTAMP)
              .setMode(Field.Mode.REQUIRED)
              .build(),
          Field.newBuilder("startUserJourneyTimestamp", StandardSQLTypeName.TIMESTAMP)
              .setMode(Field.Mode.REQUIRED)
              .build(),
          Field.newBuilder("endUserJourneyTimestamp", StandardSQLTypeName.TIMESTAMP)
              .setMode(Field.Mode.REQUIRED)
              .build(),
          Field.newBuilder(
                  "testScriptResultSummaries",
                  LegacySQLTypeName.RECORD,
                  Field.newBuilder("testScriptDescription", StandardSQLTypeName.STRING)
                      .setMode(Field.Mode.NULLABLE)
                      .build(),
                  Field.newBuilder("testScriptName", StandardSQLTypeName.STRING)
                      .setMode(Field.Mode.REQUIRED)
                      .build(),
                  Field.newBuilder(
                          "elapsedTimeStatistics",
                          LegacySQLTypeName.RECORD,
                          Field.newBuilder("min", StandardSQLTypeName.FLOAT64)
                              .setMode(Field.Mode.REQUIRED)
                              .build(),
                          Field.newBuilder("max", StandardSQLTypeName.FLOAT64)
                              .setMode(Field.Mode.REQUIRED)
                              .build(),
                          Field.newBuilder("mean", StandardSQLTypeName.FLOAT64)
                              .setMode(Field.Mode.REQUIRED)
                              .build(),
                          Field.newBuilder("standardDeviation", StandardSQLTypeName.FLOAT64)
                              .setMode(Field.Mode.REQUIRED)
                              .build(),
                          Field.newBuilder("median", StandardSQLTypeName.FLOAT64)
                              .setMode(Field.Mode.REQUIRED)
                              .build(),
                          Field.newBuilder("percentile95", StandardSQLTypeName.FLOAT64)
                              .setMode(Field.Mode.REQUIRED)
                              .build(),
                          Field.newBuilder("percentile99", StandardSQLTypeName.FLOAT64)
                              .setMode(Field.Mode.REQUIRED)
                              .build(),
                          Field.newBuilder("sum", StandardSQLTypeName.FLOAT64)
                              .setMode(Field.Mode.REQUIRED)
                              .build())
                      .setMode(Field.Mode.REQUIRED)
                      .build(),
                  Field.newBuilder("totalRun", StandardSQLTypeName.INT64)
                      .setMode(Field.Mode.NULLABLE)
                      .build(),
                  Field.newBuilder("numCompleted", StandardSQLTypeName.INT64)
                      .setMode(Field.Mode.NULLABLE)
                      .build(),
                  Field.newBuilder("numExceptionsThrown", StandardSQLTypeName.INT64)
                      .setMode(Field.Mode.NULLABLE)
                      .build(),
                  Field.newBuilder("isFailure", StandardSQLTypeName.BOOL)
                      .setMode(Field.Mode.NULLABLE)
                      .build())
              .setMode(Field.Mode.REPEATED)
              .build());

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
    String dataSet = System.getenv("DATASET");
    String table = System.getenv("TABLE");
    String bucketName = event.getBucket();
    String objectName = event.getName();

    InputStream in =
        MediaTypeUtils.getStorageObjectDataAsInputStream(projectId, bucketName, objectName);

    // Uncompress .gz as CompressorInputStream
    CompressorInputStream uncompressedInputStream = MediaTypeUtils.createCompressorInputStream(in);
    /*CompressorStreamFactory compressor = CompressorStreamFactory.getSingleton();
    CompressorInputStream uncompressedInputStream =
        in.markSupported()
            ? compressor.createCompressorInputStream(in)
            : compressor.createCompressorInputStream(new BufferedInputStream(in));*/

    // Untar .tar as ArchiveInputStream
    ArchiveInputStream archiveInputStream =
        MediaTypeUtils.createArchiveInputStream(uncompressedInputStream);
    /*ArchiveStreamFactory archiver = new ArchiveStreamFactory();
    ArchiveInputStream archiveInputStream =
        uncompressedInputStream.markSupported()
            ? archiver.createArchiveInputStream(uncompressedInputStream)
            : archiver.createArchiveInputStream(new BufferedInputStream(uncompressedInputStream));
     */
    // ArchiveInputStream is a special type of InputStream that emits an EOF when it gets to the end
    // of a file in the archive.
    // Once it’s done, call getNextEntry to reset the stream and start reading the next file.
    // When getNextEntry returns null, you’re at the end of the archive.
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

  // Open a channel to stream json string to BigQuery
  /*private void streamToBQ(String projectId, String dataset, String table, byte[] data)
      throws IOException {
    TableId tableId = TableId.of(projectId, dataset, table);
    WriteChannelConfiguration configuration =
        WriteChannelConfiguration.newBuilder(tableId)
            .setFormatOptions(FormatOptions.json())
            .setCreateDisposition(JobInfo.CreateDisposition.CREATE_NEVER)
            .build();
    BigQuery bigquery = RemoteBigQueryHelper.create().getOptions().getService();
    TableDataWriteChannel channel = bigquery.writer(configuration);

    try {
      JsonElement element = JsonParser.parseString(new String(data));
      channel.write(ByteBuffer.wrap(element.toString().getBytes(StandardCharsets.UTF_8)));
    } finally {
      channel.close();
    }
  }*/

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
   * <p>//@param projectId the Google Project ID //@param bucketName the Google Storage Bucket
   * //@param objectName the Google Storage Bucket File Object
   *
   * @return URL object
   */
  /*private InputStream getObjectAsInputStream(String projectId, String bucketName, String objectName)
      throws StorageException {
    Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
    ReadChannel reader = storage.reader(bucketName, objectName);
    return Channels.newInputStream(reader);
  }*/

  /*private byte[] readEntry(InputStream input, final long size) throws IOException {
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
  }*/
}
