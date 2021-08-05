package bio.terra.cloudfiletodatastore.deltalayer;

import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.Field;
import com.google.cloud.bigquery.StandardSQLTypeName;
import com.google.common.annotations.VisibleForTesting;
import java.util.List;
import java.util.Map;

public interface DeltaLayerBigQueryWriter {

  String EAV_TABLE_NAME = "eav_table";

  void insertRows(List<Map<String, Object>> inserts, String dataSet, BigQuery bigQuery);

  @VisibleForTesting
  default List<Field> getEavSchema() {
    return List.of(
        Field.of("datarepo_row_id", StandardSQLTypeName.STRING),
        Field.of("attribute_name", StandardSQLTypeName.STRING),
        Field.of("updated_at", StandardSQLTypeName.TIMESTAMP),
        Field.of("date_val", StandardSQLTypeName.DATE),
        Field.of("ts_val", StandardSQLTypeName.TIMESTAMP),
        Field.of("int_val", StandardSQLTypeName.INT64),
        Field.of("float_val", StandardSQLTypeName.FLOAT64),
        Field.of("str_val", StandardSQLTypeName.STRING),
        Field.of("bool_val", StandardSQLTypeName.BOOL));
  }
}
