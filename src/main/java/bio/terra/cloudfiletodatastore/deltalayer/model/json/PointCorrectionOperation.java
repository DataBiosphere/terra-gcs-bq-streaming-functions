package bio.terra.cloudfiletodatastore.deltalayer.model.json;

import java.util.UUID;

public class PointCorrectionOperation {

  private UUID datarepoRowId;

  private String name;

  private Object value;

  public UUID getDatarepoRowId() {
    return datarepoRowId;
  }

  public void setDatarepoRowId(UUID datarepoRowId) {
    this.datarepoRowId = datarepoRowId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Object getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
