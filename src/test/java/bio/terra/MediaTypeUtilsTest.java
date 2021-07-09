package bio.terra;

import static org.junit.Assert.assertTrue;

import bio.terra.cloudfunctions.utils.MediaTypeUtils;
import bio.terra.common.BaseTest;
import java.io.BufferedInputStream;
import java.io.IOException;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.junit.Test;

public class MediaTypeUtilsTest extends BaseTest {
  @Test
  public void mockTGZTest() {
    CompressorInputStream cis = null;
    ArchiveInputStream ais = null;
    BufferedInputStream bis = null;
    try {
      cis = MediaTypeUtils.createCompressorInputStream(MOCK_TGZ);
      bis = new BufferedInputStream(cis);
      ais = MediaTypeUtils.createArchiveInputStream(cis);
      ArchiveEntry archiveEntry;
      System.out.println("mockTGZTest:");
      while ((archiveEntry = ais.getNextEntry()) != null) {
        verifyMockTGZArchiveEntry(archiveEntry.getName(), archiveEntry.getSize());
      }
    } catch (Exception e) {
      System.out.println("mockTGZTest: " + e.getMessage());
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
      System.out.println("mockGZTest:");
      while ((archiveEntry = ais.getNextEntry()) != null) {
        verifyMockTGZArchiveEntry(archiveEntry.getName(), archiveEntry.getSize());
      }
    } catch (Exception e) {
      System.out.println("mockGZTest: " + e.getMessage());
    } finally {
      if (bis != null) {
        try {
          System.out.println("mockGZTest: Verifying filesize.");
          assertTrue(bis.readAllBytes().length == 1350);
        } catch (IOException e) {
          System.out.println("mockGZTest: " + e.getMessage());
        }
      }
    }
  }
}
