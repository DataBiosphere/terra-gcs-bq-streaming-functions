package bio.terra.cloudfiletodatastore.deltalayer;

import java.util.Map;

public class InsertData {

  private final String rowId;

  private final Map<String, Object> data;

  public InsertData(String rowId, Map<String, Object> data) {
    this.rowId = rowId;
    this.data = data;
  }

  public String getRowId() {
    return rowId;
  }

  public Map<String, Object> getData() {
    return data;
  }
}
