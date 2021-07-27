package bio.terra.cloudfiletodatastore.deltalayer;

import bio.terra.cloudfiletodatastore.deltalayer.model.BqInserts;
import com.google.cloud.bigquery.*;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class BigQueryWriter {

  private static final Logger logger = Logger.getLogger(BigQueryWriter.class.getName());

  private BigQuery bigquery = BigQueryOptions.getDefaultInstance().getService();

  public void insertRows(List<BqInserts> inserts) {
    for (BqInserts insert : inserts) {
      String bqDataSet = insert.getDataSet();
      String bqTable = insert.getTableName();
      List<InsertAllRequest.RowToInsert> rows = insert.getData();
      List<List<InsertAllRequest.RowToInsert>> chunks = Lists.partition(rows, 500);
      for (List<InsertAllRequest.RowToInsert> chunk : chunks) {
        InsertAllResponse insertAllResponse = bigquery.insertAll(InsertAllRequest.newBuilder(bqDataSet, bqTable, chunk).build());
        if(insertAllResponse.hasErrors()){
          Map<Long, List<BigQueryError>> insertErrors = insertAllResponse.getInsertErrors();
          for (Map.Entry<Long, List<BigQueryError>> entry : insertErrors.entrySet()) {
            logger.warning(String.format("Error for row %s is %s", entry.getKey(), entry.getValue()));
          }
        }
      }
    }
    logger.info("Done writing data to big query");
  }
}
