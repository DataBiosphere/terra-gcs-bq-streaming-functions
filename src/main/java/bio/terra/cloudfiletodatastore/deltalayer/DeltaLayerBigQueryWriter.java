package bio.terra.cloudfiletodatastore.deltalayer;

import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.Field;
import com.google.cloud.bigquery.StandardSQLTypeName;
import com.google.common.annotations.VisibleForTesting;
import java.util.List;

public interface DeltaLayerBigQueryWriter {

  String EAV_TABLE_NAME = "eav_table";

  void insertRows(List<InsertData> inserts, String dataSet, String project, BigQuery bigQuery);

  @VisibleForTesting
  default List<Field> getEavSchema() {
    return List.of(
        Field.newBuilder("datarepo_row_id", StandardSQLTypeName.STRING)
            .setMode(Field.Mode.REQUIRED)
            .build(),
        Field.newBuilder("attribute_name", StandardSQLTypeName.STRING)
            .setMode(Field.Mode.REQUIRED)
            .build(),
        Field.newBuilder("updated_at", StandardSQLTypeName.TIMESTAMP)
            .setMode(Field.Mode.REQUIRED)
            .build(),
        Field.of("date_val", StandardSQLTypeName.DATE),
        Field.of("ts_val", StandardSQLTypeName.TIMESTAMP),
        Field.of("int_val", StandardSQLTypeName.INT64),
        Field.of("float_val", StandardSQLTypeName.FLOAT64),
        Field.of("str_val", StandardSQLTypeName.STRING),
        Field.of("bool_val", StandardSQLTypeName.BOOL));
  }
}
