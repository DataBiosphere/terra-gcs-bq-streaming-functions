package bio.terra;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;

import bio.terra.cloudfunctions.utils.MediaTypeUtils;
import bio.terra.common.BaseTest;
import java.io.BufferedInputStream;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.junit.Test;

public class MediaTypeUtilsTest extends BaseTest {

  @Test
  public void mockTGZTest() {
    try {
      CompressorInputStream cis = MediaTypeUtils.createCompressorInputStream(MOCK_TGZ);
      ArchiveInputStream ais = MediaTypeUtils.createArchiveInputStream(cis);
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
    try {
      CompressorInputStream cis = MediaTypeUtils.createCompressorInputStream(MOCK_GZ);
      final BufferedInputStream bis = new BufferedInputStream(cis);
      // Expect createArchiveInputStream to throws ArchiveException because MOCK_GZ is a gz file,
      // not tar gz.
      assertThrows(
          ArchiveException.class,
          () -> {
            ArchiveInputStream ais = MediaTypeUtils.createArchiveInputStream(bis);
          });
      assertEquals(bis.readAllBytes().length, 1350);
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }
}
