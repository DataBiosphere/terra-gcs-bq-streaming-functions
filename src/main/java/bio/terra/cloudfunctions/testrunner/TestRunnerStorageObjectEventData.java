package bio.terra.cloudfunctions.testrunner;

import com.google.events.cloud.storage.v1.StorageObjectData;
import java.util.List;

/** An object that extends Google Cloud Storage Event. */
public class TestRunnerStorageObjectEventData extends StorageObjectData {
  private String projectId;
  private String dataset;
  private List<String> tables;
  /** The GCP that the event originates from. */
  public String getProjectId() {
    return projectId;
  }

  public void setProjectId(String projectId) {
    this.projectId = projectId;
  }

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
