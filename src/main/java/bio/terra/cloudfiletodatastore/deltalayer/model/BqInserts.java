package bio.terra.cloudfiletodatastore.deltalayer.model;

import com.google.cloud.bigquery.InsertAllRequest;
import java.util.List;

public class BqInserts {

  private final List<InsertAllRequest.RowToInsert> data;

  private final String tableName;

  private final String dataSet;

  public BqInserts(List<InsertAllRequest.RowToInsert> data, String tableName, String dataSet) {
    this.data = data;
    this.tableName = tableName;
    this.dataSet = dataSet;
  }

  public List<InsertAllRequest.RowToInsert> getData() {
    return data;
  }

  public String getTableName() {
    return tableName;
  }

  public String getDataSet() {
    return dataSet;
  }
}
