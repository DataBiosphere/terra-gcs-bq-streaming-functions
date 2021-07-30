package bio.terra.cloudfiletodatastore.deltalayer.model.json;

import java.time.OffsetDateTime;
import java.util.List;

public class InsertRequest {

  private String insertId;

  private InsertDestination destination;

  private List<InsertOperation> inserts;

  private InsertSource source;

  private OffsetDateTime insertTimestamp;

  public String getInsertId() {
    return insertId;
  }

  public void setInsertId(String insertId) {
    this.insertId = insertId;
  }

  public InsertDestination getDestination() {
    return destination;
  }

  public void setDestination(InsertDestination destination) {
    this.destination = destination;
  }

  public List<InsertOperation> getInserts() {
    return inserts;
  }

  public void setInserts(List<InsertOperation> inserts) {
    this.inserts = inserts;
  }

  public InsertSource getSource() {
    return source;
  }

  public void setSource(InsertSource source) {
    this.source = source;
  }

  public OffsetDateTime getInsertTimestamp() {
    return insertTimestamp;
  }

  public void setInsertTimestamp(OffsetDateTime insertTimestamp) {
    this.insertTimestamp = insertTimestamp;
  }
}
