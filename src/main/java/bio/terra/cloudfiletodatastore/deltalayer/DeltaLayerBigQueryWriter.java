package bio.terra.cloudfiletodatastore.deltalayer;

import com.google.cloud.bigquery.*;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class DeltaLayerBigQueryWriter {

  private static final Logger logger = Logger.getLogger(DeltaLayerBigQueryWriter.class.getName());

  private static final String EAV_TABLE_NAME = "eav_table";

  public void insertRows(
      List<InsertAllRequest.RowToInsert> inserts, String dataSet, BigQuery bigQuery) {
    List<List<InsertAllRequest.RowToInsert>> chunks = Lists.partition(inserts, 500);
    for (List<InsertAllRequest.RowToInsert> chunk : chunks) {
      InsertAllResponse insertAllResponse =
          bigQuery.insertAll(InsertAllRequest.newBuilder(dataSet, EAV_TABLE_NAME, chunk).build());
      if (insertAllResponse.hasErrors()) {
        Map<Long, List<BigQueryError>> insertErrors = insertAllResponse.getInsertErrors();
        for (Map.Entry<Long, List<BigQueryError>> entry : insertErrors.entrySet()) {
          logger.warning(String.format("Error for row %s is %s", entry.getKey(), entry.getValue()));
        }
      }
    }
    logger.info("Done writing data to big query");
  }
}
