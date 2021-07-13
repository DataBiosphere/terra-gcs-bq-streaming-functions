package bio.terra.cloudfunctions.testrunner;

import com.google.events.cloud.storage.v1.StorageObjectData;
import java.util.List;

/** An object that extends Google Cloud Storage Event. */
public class TestRunnerStorageObjectEvent extends StorageObjectData {
  private String dataset;
  private List<String> tables;

  /** The name of the BigQuery dataset for streaming data. */
  public String getDataset() {
    return dataset;
  }

  public void setDataset(String dataset) {
    this.dataset = dataset;
  }

  /** A list of table names associated with the BigQuery dataset. */
  public List<String> getTables() {
    return tables;
  }

  public void setTables(List<String> tables) {
    this.tables = tables;
  }
}
