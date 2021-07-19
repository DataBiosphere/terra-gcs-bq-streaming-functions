package bio.terra;

import bio.terra.common.BaseTest;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.v1.CloudEventV1;
import org.junit.Test;
import org.openjdk.jol.vm.VM;

public class CloudEventHarnessTest extends BaseTest {
  @Test
  public void StorageObjectEventMessageTest() {
    CloudEvent e1 = new CloudEventV1("", null, "", "", null, "", null, null, null);
    CloudEvent e2 = new CloudEventV1("", null, "", "", null, "", null, null, null);
    System.out.println(VM.current().addressOf(e1.getClass()));
    System.out.println(VM.current().addressOf(e2.getClass()));
    System.out.println(VM.current().addressOf(CloudEventV1.class));
  }
}
