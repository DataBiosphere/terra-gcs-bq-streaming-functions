package bio.terra.cloudfiletodatastore.deltalayer.model;

public class PointCorrectionOperation {

  String op;
  String attributeName;
  String addUpdateAttribute;

  public String getOp() {
    return op;
  }

  public void setOp(String op) {
    this.op = op;
  }

  public String getAttributeName() {
    return attributeName;
  }

  public void setAttributeName(String attributeName) {
    this.attributeName = attributeName;
  }

  public String getAddUpdateAttribute() {
    return addUpdateAttribute;
  }

  public void setAddUpdateAttribute(String addUpdateAttribute) {
    this.addUpdateAttribute = addUpdateAttribute;
  }
}
