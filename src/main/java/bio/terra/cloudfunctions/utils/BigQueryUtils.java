package bio.terra.cloudfunctions.utils;

import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.FormatOptions;
import com.google.cloud.bigquery.JobInfo;
import com.google.cloud.bigquery.TableDataWriteChannel;
import com.google.cloud.bigquery.TableId;
import com.google.cloud.bigquery.WriteChannelConfiguration;
import com.google.gson.JsonElement;
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
    TableId tableId = TableId.of(projectId, dataset, table);
    WriteChannelConfiguration configuration =
        WriteChannelConfiguration.newBuilder(tableId)
            .setFormatOptions(FormatOptions.json())
            .setCreateDisposition(JobInfo.CreateDisposition.CREATE_NEVER)
            .build();
    BigQuery bigquery = BigQueryOptions.getDefaultInstance().getService();

    try (TableDataWriteChannel channel = bigquery.writer(configuration)) {
      // This step removes pretty formatting from the raw json data before streaming takes place.
      JsonElement element = JsonParser.parseString(new String(data));
      channel.write(ByteBuffer.wrap(element.toString().getBytes(StandardCharsets.UTF_8)));
    } catch (JsonSyntaxException e) {
      logger.log(Level.SEVERE, "Invalid Json data: " + new String(data));
    }
  }
}
