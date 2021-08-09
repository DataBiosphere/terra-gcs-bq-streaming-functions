package bio.terra.cloudfiletodatastore.deltalayer;

import com.google.cloud.bigquery.*;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Does the Big Query things, namely create the EAV table and insert to the EAV table. */
public class DeltaLayerBQSQLWriter implements DeltaLayerBigQueryWriter {

  private static final Logger logger = Logger.getLogger(DeltaLayerBQSQLWriter.class.getName());

  public void createEavTableIfNeeded(BigQuery bigQuery, String dataSet, String project) {
    if (!eavTableExists(bigQuery, project, dataSet)) {
      createEavTable(bigQuery, project, dataSet);
    }
  }

  private void createEavTable(BigQuery bigQuery, String dataSet, String project) {
    logger.info("About to create EAV table");
    TableId tableId = TableId.of(project, dataSet, EAV_TABLE_NAME);
    TableDefinition tableDefinition = StandardTableDefinition.of(Schema.of(getEavSchema()));
    TableInfo tableInfo = TableInfo.newBuilder(tableId, tableDefinition).build();
    bigQuery.create(tableInfo);
    logger.info("EAV table created");
  }

  private boolean eavTableExists(BigQuery bigQuery, String project, String dataSet) {
    String query =
        String.format(
            "select TABLE_NAME from `%s`.%s.INFORMATION_SCHEMA.TABLES where TABLE_NAME = '%s'",
            project, dataSet, EAV_TABLE_NAME);
    QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(query).build();

    try {
      return bigQuery.query(queryConfig).getTotalRows() == 1;
    } catch (InterruptedException e) {
      logger.log(Level.SEVERE, "Could not query for EAV table's existence", e);
      return false;
    }
  }

  @Override
  public void insertRows(
      List<Map<String, Object>> inserts, String dataSet, String project, BigQuery bigQuery) {
    createEavTableIfNeeded(bigQuery, dataSet, project);
    for (Map<String, Object> insert : inserts) {
      InsertAllResponse insertAllResponse =
          bigQuery.insertAll(
              InsertAllRequest.newBuilder(
                      TableId.of(project, dataSet, EAV_TABLE_NAME),
                      InsertAllRequest.RowToInsert.of(insert))
                  .build());
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
