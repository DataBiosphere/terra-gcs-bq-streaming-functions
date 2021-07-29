package bio.terra.cloudfiletodatastore.deltalayer.model.json;

import java.util.UUID;

public class InsertDestination {

  private String bqDataset;

  private UUID workspaceId;

  private String datasetProject;

  public String getBqDataset() {
    return bqDataset;
  }

  public void setBqDataset(String bqDataset) {
    this.bqDataset = bqDataset;
  }

  public UUID getWorkspaceId() {
    return workspaceId;
  }

  public void setWorkspaceId(UUID workspaceId) {
    this.workspaceId = workspaceId;
  }

  public String getDatasetProject() {
    return datasetProject;
  }

  public void setDatasetProject(String datasetProject) {
    this.datasetProject = datasetProject;
  }
}
