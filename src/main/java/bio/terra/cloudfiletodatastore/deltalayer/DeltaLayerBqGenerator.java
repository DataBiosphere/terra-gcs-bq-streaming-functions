package bio.terra.cloudfiletodatastore.deltalayer;

import bio.terra.cloudfiletodatastore.BigQueryInsertGenerator;
import bio.terra.cloudfiletodatastore.deltalayer.model.BqInserts;
import bio.terra.cloudfiletodatastore.deltalayer.model.PointCorrection;
import com.google.cloud.bigquery.InsertAllRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DeltaLayerBqGenerator implements BigQueryInsertGenerator<List<PointCorrection>> {

  @Override
  public List<BqInserts> getInserts(List<PointCorrection> toConvert) {
    List<BqInserts> result = new ArrayList<>();
    for (PointCorrection pointCorrection : toConvert) {
      List<InsertAllRequest.RowToInsert> inserts =
          pointCorrection.getOperations().stream()
              .map(
                  s -> {
                    Map<String, Object> data = new HashMap<>();
                    data.put("attribute_name", s.getAttributeName());
                    data.put("add_update_attribute", s.getAddUpdateAttribute());

                    return InsertAllRequest.RowToInsert.of(pointCorrection.getEntityType(), data);
                  })
              .collect(Collectors.toList());
      result.add(
          new BqInserts(inserts, pointCorrection.getEntityType(), pointCorrection.getName()));
    }
    return result;
  }
}
