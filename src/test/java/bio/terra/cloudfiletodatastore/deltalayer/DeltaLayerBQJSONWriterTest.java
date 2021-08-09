package bio.terra.cloudfiletodatastore.deltalayer;

import static junit.framework.TestCase.assertEquals;

import com.google.cloud.bigquery.Schema;
import com.google.cloud.bigquery.TableId;
import com.google.cloud.bigquery.WriteChannelConfiguration;
import java.util.Map;
import org.junit.Test;

public class DeltaLayerBQJSONWriterTest {

  @Test
  public void writeConfiguration() {
    DeltaLayerBQJSONWriter writerToTest = new DeltaLayerBQJSONWriter();
    String dataSet = "point_correction";
    String project = "project";
    WriteChannelConfiguration writeChannelConfiguration =
        writerToTest.getWriteChannelConfiguration(dataSet, project);
    assertEquals(writeChannelConfiguration.getSchema(), Schema.of(writerToTest.getEavSchema()));
    assertEquals(
        writeChannelConfiguration.getDestinationTable(),
        TableId.of(project, dataSet, DeltaLayerBigQueryWriter.EAV_TABLE_NAME));
  }

  @Test
  public void writeBytes() {
    DeltaLayerBQJSONWriter writerToTest = new DeltaLayerBQJSONWriter();
    byte[] bytesForInsert = writerToTest.getBytesForInsert(Map.of("bool_val", false));
    assertEquals("{\"bool_val\":false}", new String(bytesForInsert));
  }
}
