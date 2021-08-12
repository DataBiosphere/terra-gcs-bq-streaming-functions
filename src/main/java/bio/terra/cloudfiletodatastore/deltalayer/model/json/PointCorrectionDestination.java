package bio.terra.cloudfiletodatastore.deltalayer.model.json;

import java.util.Objects;
import java.util.UUID;

public class PointCorrectionDestination {

  private String bqDataset;

  private UUID workspaceId;

  private String datasetProject;

  // for ease of testing verification
  public PointCorrectionDestination(String bqDataset, UUID workspaceId, String datasetProject) {
    this.bqDataset = bqDataset;
    this.workspaceId = workspaceId;
    this.datasetProject = datasetProject;
  }

  // for Gson
  public PointCorrectionDestination() {}

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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof PointCorrectionDestination)) return false;
    PointCorrectionDestination that = (PointCorrectionDestination) o;
    return Objects.equals(getBqDataset(), that.getBqDataset())
        && Objects.equals(getWorkspaceId(), that.getWorkspaceId())
        && Objects.equals(getDatasetProject(), that.getDatasetProject());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getBqDataset(), getWorkspaceId(), getDatasetProject());
  }
}
