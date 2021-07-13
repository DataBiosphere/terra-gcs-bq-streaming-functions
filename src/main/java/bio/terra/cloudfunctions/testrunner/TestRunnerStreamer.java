package bio.terra.cloudfunctions.testrunner;

import bio.terra.cloudfunctions.common.ContentHandler;
import bio.terra.cloudfunctions.utils.BigQueryUtils;
import bio.terra.cloudfunctions.utils.MediaTypeUtils;
import java.util.Arrays;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestRunnerStreamer extends ContentHandler<TestRunnerStorageObjectEventData> {
  private static final Logger logger = LoggerFactory.getLogger(TestRunnerStreamer.class);

  @Override
  public void translate() throws Exception {
    getEvent().setProjectId(System.getenv("GCLOUD_PROJECT"));
    getEvent().setDataset(System.getenv("DATASET"));
    getEvent().setTables(Arrays.asList(System.getenv("TABLE")));
    ArchiveInputStream ais = (ArchiveInputStream) getDataStream();
    ArchiveEntry archiveEntry;
    while ((archiveEntry = ais.getNextEntry()) != null) {
      if (!archiveEntry.isDirectory() && isPublishable(archiveEntry.getName())) {
        logger.info(
            "Processing " + archiveEntry.getName() + " " + archiveEntry.getSize() + " bytes");
        byte[] data = MediaTypeUtils.readEntry(ais, archiveEntry.getSize());
        insert(getTable(archiveEntry.getName()), data);
      }
    }
  }

  private boolean isPublishable(String entryName) {
    for (String table : getEvent().getTables()) {
      if (entryName.contains(table)) return true;
    }
    return false;
  }

  private String getTable(String entryName) {
    for (String table : getEvent().getTables()) {
      if (entryName.contains(table)) return table;
    }
    return null;
  }

  @Override
  public void insert(String table, byte[] data) throws Exception {
    BigQueryUtils.streamToBQ(getEvent().getProjectId(), getEvent().getDataset(), table, data);
  }
}
