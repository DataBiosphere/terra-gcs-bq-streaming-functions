package bio.terra.cloudfunctions.utils;

import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.FormatOptions;
import com.google.cloud.bigquery.JobInfo;
import com.google.cloud.bigquery.TableDataWriteChannel;
import com.google.cloud.bigquery.TableId;
import com.google.cloud.bigquery.WriteChannelConfiguration;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BigQueryUtils {
  private static final Logger logger = Logger.getLogger(BigQueryUtils.class.getName());
  /**
   * Stream the data representing the BigQuery table rows.
   *
   * @param projectId the GCP project to which the BigQuery dataset resides
   * @param dataset the name of the BigQuery dataset
   * @param table the name of the BigQuery table to stream data to
   * @param data the JSON data (in binary byte[] format) representing rows matching the table schema
   */
  public static void streamToBQ(String projectId, String dataset, String table, byte[] data)
      throws IOException {
    // remove any formatting from the incoming byte array, which we expect to be json
    byte[] compactData =
        JsonParser.parseString(new String(data)).toString().getBytes(StandardCharsets.UTF_8);

    TableId tableId = TableId.of(projectId, dataset, table);
    WriteChannelConfiguration configuration =
        WriteChannelConfiguration.newBuilder(tableId)
            .setFormatOptions(FormatOptions.json())
            .setCreateDisposition(JobInfo.CreateDisposition.CREATE_NEVER)
            .build();
    BigQuery bigquery = BigQueryOptions.newBuilder().setProjectId(projectId).build().getService();

    try (TableDataWriteChannel channel = bigquery.writer(configuration)) {
      channel.write(ByteBuffer.wrap(compactData));
    } catch (JsonSyntaxException e) {
      logger.log(Level.SEVERE, "Invalid Json data: " + new String(data));
    }
  }
}
