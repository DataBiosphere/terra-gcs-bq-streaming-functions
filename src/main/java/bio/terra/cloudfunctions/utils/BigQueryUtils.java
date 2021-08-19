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
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class BigQueryUtils {
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
    TableDataWriteChannel channel = bigquery.writer(configuration);

    try {
      JsonElement element = JsonParser.parseString(new String(data));
      channel.write(ByteBuffer.wrap(element.toString().getBytes(StandardCharsets.UTF_8)));
    } finally {
      channel.close();
    }
  }
}
