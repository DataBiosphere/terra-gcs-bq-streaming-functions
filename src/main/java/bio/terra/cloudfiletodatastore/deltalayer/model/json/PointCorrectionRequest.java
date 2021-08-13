package bio.terra.cloudfiletodatastore.deltalayer.model.json;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * This class as well as the others in this package are used to deserialize delta layer json using
 * Google's Gson library. files:
 *
 * <p>{ "destination": { "bqDataset": "point_correction", "datasetProject":
 * "delta-layer-schema-tests", "workspaceId": "fe2dfaa7-e3a8-4725-b652-499f4eb2c88d" }, "insertId":
 * "d3b7b01f-952e-4d5e-b4c2-548c21409da0", "insertTimestamp": "2021-07-23T14:23:33.060729Z",
 * "inserts": [{ "datarepoRowId": "1f1d1041-bb84-4cc5-bb86-16fd40cc9a34", "name":
 * "my_new_bool_column", "value": "false" }], "source": { "insertingUser": "111137720113699110757",
 * "referenceId": "6d1f2766-f46c-4427-acc2-00caf089b37e" } }
 */
public class PointCorrectionRequest {

  private UUID insertId;

  private PointCorrectionDestination destination;

  private List<PointCorrectionOperation> inserts;

  private PointCorrectionSource source;

  private OffsetDateTime insertTimestamp;

  public UUID getInsertId() {
    return insertId;
  }

  public void setInsertId(UUID insertId) {
    this.insertId = insertId;
  }

  public PointCorrectionDestination getDestination() {
    return destination;
  }

  public void setDestination(PointCorrectionDestination destination) {
    this.destination = destination;
  }

  public List<PointCorrectionOperation> getInserts() {
    return inserts;
  }

  public void setInserts(List<PointCorrectionOperation> inserts) {
    this.inserts = inserts;
  }

  public PointCorrectionSource getSource() {
    return source;
  }

  public void setSource(PointCorrectionSource source) {
    this.source = source;
  }

  public OffsetDateTime getInsertTimestamp() {
    return insertTimestamp;
  }

  public void setInsertTimestamp(OffsetDateTime insertTimestamp) {
    this.insertTimestamp = insertTimestamp;
  }
}
