package bio.terra.cloudfunctions.testrunner;

import bio.terra.cloudfunctions.common.ContentHandler;
import com.google.events.cloud.storage.v1.StorageObjectData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestRunnerStreamer extends ContentHandler<StorageObjectData> {
  private static final Logger logger = LoggerFactory.getLogger(TestRunnerStreamer.class);

  @Override
  public void translate() throws Exception {
    logger.debug("translate");
    super.translate();
  }

  @Override
  public void insert() throws Exception {
    logger.debug("insert");
    super.insert();
  }
}
