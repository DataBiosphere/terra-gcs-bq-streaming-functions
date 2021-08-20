package bio.terra;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import bio.terra.cloudfunctions.utils.MediaTypeUtils;
import bio.terra.common.BaseTest;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.logging.Logger;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.junit.Test;

public class MediaTypeUtilsTest extends BaseTest {
  private static final Logger logger = Logger.getLogger(MediaTypeUtilsTest.class.getName());

  @Test
  public void mockTGZTest() {
    CompressorInputStream cis = null;
    ArchiveInputStream ais = null;
    try {
      cis = MediaTypeUtils.createCompressorInputStream(MOCK_TGZ);
      ais = MediaTypeUtils.createArchiveInputStream(cis);
      ArchiveEntry archiveEntry;
      int numberOfFilesProcessed = 0;
      while ((archiveEntry = ais.getNextEntry()) != null) {
        if (!archiveEntry.isDirectory()) {
          numberOfFilesProcessed++;
          verifyMockTGZArchiveEntry(archiveEntry.getName(), archiveEntry.getSize());
        }
      }
      assertEquals("unexpected number of archive entries processed", 3, numberOfFilesProcessed);
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void mockGZTest() {
    CompressorInputStream cis = null;
    ArchiveInputStream ais = null;
    BufferedInputStream bis = null;
    try {
      cis = MediaTypeUtils.createCompressorInputStream(MOCK_GZ);
      bis = new BufferedInputStream(cis);
      ais = MediaTypeUtils.createArchiveInputStream(bis);
      ArchiveEntry archiveEntry;
      while ((archiveEntry = ais.getNextEntry()) != null) {
        if (!archiveEntry.isDirectory()) {
          verifyMockTGZArchiveEntry(archiveEntry.getName(), archiveEntry.getSize());
        }
      }
    } catch (Exception e) {
      assertNull(ais);
      assertNotNull(bis);
    } finally {
      if (bis != null) {
        try {
          assertEquals(bis.readAllBytes().length, 1350);
        } catch (IOException e) {
          fail(e.getMessage());
        }
      }
    }
  }
}
