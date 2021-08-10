package bio.terra;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import bio.terra.cloudfunctions.common.FileTypeDetector;
import bio.terra.cloudfunctions.common.GsonWrapper;
import bio.terra.common.BaseTest;
import com.google.events.cloud.storage.v1.StorageObjectData;
import java.io.BufferedInputStream;
import java.util.logging.Logger;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.junit.Test;

public class FileTypeDetectorTest extends BaseTest {
  private static final Logger logger = Logger.getLogger(FileTypeDetectorTest.class.getName());

  @Test
  public void tarGzipHandlerTest() {
    try {
      StorageObjectData storageObjectData =
          GsonWrapper.convertFromClass(MOCK_EVENT_GZIP, StorageObjectData.class);
      FileTypeDetector fileTypeDetector = new FileTypeDetector(storageObjectData);
      // Set a mock TGZ input stream to simulate the GCS input stream
      fileTypeDetector.setInputStream(MOCK_TGZ);
      fileTypeDetector.handleMediaType();
      ArchiveInputStream ais = (ArchiveInputStream) fileTypeDetector.getDataStream();
      ArchiveEntry archiveEntry;
      logger.info("gzipHandlerTest:");
      while ((archiveEntry = ais.getNextEntry()) != null) {
        logger.info("Verifying " + archiveEntry.getName() + " filesize.");
        if (!archiveEntry.isDirectory())
          verifyMockTGZArchiveEntry(archiveEntry.getName(), archiveEntry.getSize());
      }
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void gzipHandlerTest() {
    try {
      StorageObjectData storageObjectData =
          GsonWrapper.convertFromClass(MOCK_EVENT_GZIP, StorageObjectData.class);
      FileTypeDetector fileTypeDetector = new FileTypeDetector(storageObjectData);
      // Set a mock GZ input stream to simulate the GCS input stream
      fileTypeDetector.setInputStream(MOCK_GZ);
      fileTypeDetector.handleMediaType();
      BufferedInputStream bis = (BufferedInputStream) fileTypeDetector.getDataStream();
      logger.info("gzipHandlerTest2: Verifying filesize.");
      assertEquals(bis.readAllBytes().length, 1350);
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void jsonHandlerTest() {
    try {
      StorageObjectData storageObjectData =
          GsonWrapper.convertFromClass(MOCK_EVENT_JSON, StorageObjectData.class);
      FileTypeDetector fileTypeDetector = new FileTypeDetector(storageObjectData);
      // Set a mock JSON input stream to simulate the GCS input stream
      fileTypeDetector.setInputStream(MOCK_JSON);
      fileTypeDetector.handleMediaType();
      BufferedInputStream bis = (BufferedInputStream) fileTypeDetector.getDataStream();
      logger.info("jsonHandlerTest: Verifying filesize.");
      assertEquals(bis.readAllBytes().length, 1351);
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }
}
