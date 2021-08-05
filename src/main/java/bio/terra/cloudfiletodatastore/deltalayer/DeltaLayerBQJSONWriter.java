package bio.terra.cloudfiletodatastore.deltalayer;

import bio.terra.cloudfunctions.common.GsonWrapper;
import com.google.cloud.bigquery.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Does the Big Query things, namely create the EAV table and insert to the EAV table. */
public class DeltaLayerBQJSONWriter implements DeltaLayerBigQueryWriter {

  private static final Logger logger = Logger.getLogger(DeltaLayerBQJSONWriter.class.getName());

  @Override
  public void insertRows(List<Map<String, Object>> inserts, String dataSet, BigQuery bigQuery) {
    TableId tableId = TableId.of(dataSet, EAV_TABLE_NAME);
    WriteChannelConfiguration writeChannelConfiguration =
        WriteChannelConfiguration.newBuilder(tableId)
            .setCreateDisposition(JobInfo.CreateDisposition.CREATE_IF_NEEDED)
            .setFormatOptions(FormatOptions.json())
            .setSchema(Schema.of(getEavSchema()))
            .build();

    for (Map<String, Object> insert : inserts) {
      String json = GsonWrapper.getInstance().toJson(insert);
      logger.info(String.format("Submitting JSON %s", json));
      byte[] jsonBytes = json.getBytes(StandardCharsets.UTF_8);
      TableDataWriteChannel writer = bigQuery.writer(writeChannelConfiguration);
      try {
        writer.write(ByteBuffer.wrap(jsonBytes));
        writer.close();
      } catch (IOException e) {
        logger.log(Level.SEVERE, "Could not write to BQ", e);
        return;
      }
      try {
        Job job = writer.getJob().waitFor();
        JobStatus status = job.getStatus();
        if (status.getError() != null) {
          logger.log(
              Level.SEVERE,
              "There was an error writing to BQ: {0}, error list: {1}",
              new Object[] {status.getError(), status.getExecutionErrors()});
        }
      } catch (InterruptedException e) {
        logger.log(Level.SEVERE, "Thread was interrupted while waiting for job to finish", e);
      }
    }
    logger.info("Done writing data to big query");
  }
}
