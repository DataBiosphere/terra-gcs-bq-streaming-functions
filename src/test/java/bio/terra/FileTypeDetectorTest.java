package bio.terra;

import static org.junit.Assert.assertTrue;

import bio.terra.cloudfunctions.common.FileTypeDetector;
import bio.terra.common.BaseTest;
import com.google.events.cloud.storage.v1.StorageObjectData;
import java.io.BufferedInputStream;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.junit.Test;

public class FileTypeDetectorTest extends BaseTest {
  @Test
  public void gzipHandlerTest() {
    FileTypeDetectorImpl fileTypeDetector = new FileTypeDetectorImpl();
    try {
      // Set a mock TGZ input stream to simulate the GCS input stream
      fileTypeDetector.setInputStream(MOCK_TGZ);
      fileTypeDetector.accept(MOCK_EVENT_GZIP, FAKE_CLOUD_FUNCTION_CONTEXT);
      ArchiveInputStream ais = (ArchiveInputStream) fileTypeDetector.getDataStream();
      ArchiveEntry archiveEntry;
      System.out.println("gzipHandlerTest:");
      while ((archiveEntry = ais.getNextEntry()) != null) {
        verifyMockTGZArchiveEntry(archiveEntry.getName(), archiveEntry.getSize());
      }
    } catch (Exception e) {
    }
  }

  @Test
  public void gzipHandlerTest2() {
    FileTypeDetectorImpl fileTypeDetector = new FileTypeDetectorImpl();
    try {
      // Set a mock GZ input stream to simulate the GCS input stream
      fileTypeDetector.setInputStream(MOCK_GZ);
      fileTypeDetector.accept(MOCK_EVENT_GZIP, FAKE_CLOUD_FUNCTION_CONTEXT);
      BufferedInputStream bis = (BufferedInputStream) fileTypeDetector.getDataStream();
      System.out.println("gzipHandlerTest2: Verifying filesize.");
      assertTrue(bis.readAllBytes().length == 1350);
    } catch (Exception e) {
    }
  }

  @Test
  public void jsonHandlerTest() {
    FileTypeDetectorImpl fileTypeDetector = new FileTypeDetectorImpl();
    try {
      // Set a mock JSON input stream to simulate the GCS input stream
      fileTypeDetector.setInputStream(MOCK_JSON);
      fileTypeDetector.accept(MOCK_EVENT_JSON, FAKE_CLOUD_FUNCTION_CONTEXT);
      BufferedInputStream bis = (BufferedInputStream) fileTypeDetector.getDataStream();
      System.out.println("jsonHandlerTest: Verifying filesize.");
      assertTrue(bis.readAllBytes().length == 1350);
    } catch (Exception e) {
    }
  }

  class FileTypeDetectorImpl extends FileTypeDetector<StorageObjectData> {}
}
