package bio.terra.cloudfiletodatastore.deltalayer.model.json;

import java.math.BigInteger;
import java.util.UUID;

public class InsertSource {

  private BigInteger insertingUser;

  private UUID referenceId;

  public BigInteger getInsertingUser() {
    return insertingUser;
  }

  public void setInsertingUser(BigInteger insertingUser) {
    this.insertingUser = insertingUser;
  }

  public UUID getReferenceId() {
    return referenceId;
  }

  public void setReferenceId(UUID referenceId) {
    this.referenceId = referenceId;
  }
}
