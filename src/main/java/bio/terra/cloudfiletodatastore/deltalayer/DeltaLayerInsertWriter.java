package bio.terra.cloudfiletodatastore.deltalayer;

import com.google.cloud.bigquery.InsertAllRequest;

import java.util.List;

public interface DeltaLayerInsertWriter {

    void insertRows(List<InsertAllRequest.RowToInsert> inserts, String dataSet, String project);
}
