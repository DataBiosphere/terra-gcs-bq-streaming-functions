package bio.terra.cloudfiletodatastore.deltalayer;

import com.google.cloud.bigquery.*;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Does the Big Query things, namely create the EAV table and insert to the EAV table. */
public class DeltaLayerBigQueryWriter {

  private static final Logger logger = Logger.getLogger(DeltaLayerBigQueryWriter.class.getName());

  private static final String EAV_TABLE_NAME = "eav_table";

  public void createEavTableIfNeeded(BigQuery bigQuery, String dataSet) {
    if (!eavTableExists(bigQuery, dataSet)) {
      createEavTable(bigQuery, dataSet);
    }
  }

  private void createEavTable(BigQuery bigQuery, String dataSet) {
    logger.info("About to create EAV table");
    TableId tableId = TableId.of(dataSet, EAV_TABLE_NAME);
    TableDefinition tableDefinition = StandardTableDefinition.of(Schema.of(getEavSchema()));
    TableInfo tableInfo = TableInfo.newBuilder(tableId, tableDefinition).build();
    bigQuery.create(tableInfo);
    logger.info("EAV table created");
  }

  @VisibleForTesting
  List<Field> getEavSchema() {
    return List.of(
        Field.of("datarepo_row_id", StandardSQLTypeName.STRING),
        Field.of("attribute_name", StandardSQLTypeName.STRING),
        Field.of("updated_at", StandardSQLTypeName.TIMESTAMP),
        Field.of("date_val", StandardSQLTypeName.DATETIME),
        Field.of("int_val", StandardSQLTypeName.INT64),
        Field.of("float_val", StandardSQLTypeName.FLOAT64),
        Field.of("str_val", StandardSQLTypeName.STRING),
        Field.of("bool_val", StandardSQLTypeName.BOOL));
  }

  private boolean eavTableExists(BigQuery bigQuery, String dataSet) {
    String query =
        String.format(
            "select TABLE_NAME from %s.INFORMATION_SCHEMA.TABLES where TABLE_NAME = '%s'",
            dataSet, EAV_TABLE_NAME);
    QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(query).build();
    try {
      return bigQuery.query(queryConfig).getTotalRows() == 1;
    } catch (InterruptedException e) {
      logger.log(Level.SEVERE, "Could not query for EAV table's existence", e);
      return false;
    }
  }

  public void insertRows(
      List<InsertAllRequest.RowToInsert> inserts, String dataSet, BigQuery bigQuery) {
    createEavTableIfNeeded(bigQuery, dataSet);
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
