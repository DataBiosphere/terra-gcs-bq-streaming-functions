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

@Deprecated
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

    // Untar .tar as ArchiveInputStream
    ArchiveInputStream archiveInputStream =
        MediaTypeUtils.createArchiveInputStream(uncompressedInputStream);

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
}
