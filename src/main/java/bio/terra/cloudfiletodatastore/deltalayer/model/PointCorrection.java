package bio.terra.cloudfiletodatastore.deltalayer.model;

import java.util.List;

public class PointCorrection {

  String name;
  String entityType;
  List<PointCorrectionOperation> operations;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEntityType() {
    return entityType;
  }

  public void setEntityType(String entityType) {
    this.entityType = entityType;
  }

  public List<PointCorrectionOperation> getOperations() {
    return operations;
  }

  public void setOperations(List<PointCorrectionOperation> operations) {
    this.operations = operations;
  }
}
