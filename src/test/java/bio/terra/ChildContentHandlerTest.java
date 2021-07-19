package bio.terra;

import bio.terra.cloudfunctions.common.ContentHandler;
import bio.terra.common.BaseTest;
import org.junit.Test;

public class ChildContentHandlerTest extends BaseTest {
  @Test
  public void acceptEventTest() {
    ChildContentHandler handler = new ChildContentHandler();
    try {
      handler.setInputStream(MOCK_TGZ);
      // handler.accept(MOCK_EVENT_GZIP, FAKE_CLOUD_FUNCTION_CONTEXT);
      // assertEvent(handler.getEvent());
    } catch (Exception e) {
    }
  }

  class ChildContentHandler extends ContentHandler {
    @Override
    public void translate() throws Exception {}

    @Override
    public void insert() throws Exception {}
  }
}
