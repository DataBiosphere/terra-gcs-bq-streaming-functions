package bio.terra;

import static org.junit.Assert.assertTrue;

import bio.terra.common.BaseTest;
import org.junit.Test;

public class MediaTypeWrapperTest extends BaseTest {
  @Test
  public void gzipTest() {
    assertTrue(APPLICATION_GZIP.isApplicationGzip());
  }

  @Test
  public void xgzipTest() {
    assertTrue(APPLICATION_X_GZIP.isApplicationGzip());
  }

  @Test
  public void jsonTest() {
    assertTrue(APPLICATION_JSON.isApplicationJson());
  }
}
