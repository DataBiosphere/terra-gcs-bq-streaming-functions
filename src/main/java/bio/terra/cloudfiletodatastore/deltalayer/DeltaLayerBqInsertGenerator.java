package bio.terra.cloudfiletodatastore.deltalayer;

import bio.terra.cloudfiletodatastore.deltalayer.model.json.InsertOperation;
import com.google.cloud.bigquery.InsertAllRequest;
import java.time.OffsetDateTime;
import java.util.*;
import org.apache.commons.validator.GenericValidator;

public class DeltaLayerBqInsertGenerator {

  private Map<Class, String> typeColumns =
      Map.of(String.class, "str_val", Integer.class, "int_val", Double.class, "float_val");

  public List<InsertAllRequest.RowToInsert> getInserts(
      List<InsertOperation> toConvert, OffsetDateTime insertTimeStamp) {
    List<InsertAllRequest.RowToInsert> inserts = new ArrayList<>();
    for (InsertOperation insert : toConvert) {
      Map<String, Object> data = new HashMap<>();
      data.put("dataRepoRowId", insert.getDatarepoRowId().toString());
      data.put("attribute_name", insert.getName());
      data.put("updated_at", insertTimeStamp.toString());
      data.put(getTargetColumn(insert.getValue()), getTypedValue(insert.getValue()));
      inserts.add(InsertAllRequest.RowToInsert.of(data));
    }
    return inserts;
  }

  private Object getTypedValue(String value) {
    if (GenericValidator.isDouble(value)) {
      return Double.valueOf(value);
    }
    if (GenericValidator.isInt(value)) {
      return Integer.valueOf(value);
    }
    if ("false".equals(value.toLowerCase(Locale.ROOT))
        || "true".equals(value.toLowerCase(Locale.ROOT))) {
      return Boolean.valueOf(value);
    }
    // TODO: handle dates
    return value;
  }

  private String getTargetColumn(String value) {
    Object typedValue = getTypedValue(value);
    return typeColumns.get(typedValue.getClass());
  }
}
