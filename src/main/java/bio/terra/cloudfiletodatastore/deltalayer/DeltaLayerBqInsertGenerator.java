package bio.terra.cloudfiletodatastore.deltalayer;

import bio.terra.cloudfiletodatastore.deltalayer.model.json.InsertOperation;
import com.google.cloud.bigquery.InsertAllRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeltaLayerBqInsertGenerator {

  public List<InsertAllRequest.RowToInsert> getInserts(List<InsertOperation> toConvert) {
    List<InsertAllRequest.RowToInsert> inserts = new ArrayList<>();
    for (InsertOperation insert : toConvert) {
      Map<String, Object> data = new HashMap<>();
      data.put("dataRepoRowId", insert.getDatarepoRowId().toString());
      data.put("col_name", insert.getName());
      data.put("col_val", insert.getName());
      inserts.add(InsertAllRequest.RowToInsert.of(insert.getDatarepoRowId().toString(), data));
    }
    return inserts;
  }
}
