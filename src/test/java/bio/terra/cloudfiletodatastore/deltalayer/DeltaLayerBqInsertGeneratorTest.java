package bio.terra.cloudfiletodatastore.deltalayer;

import static junit.framework.TestCase.assertEquals;

import bio.terra.cloudfiletodatastore.GsonConverter;
import bio.terra.cloudfiletodatastore.deltalayer.model.json.InsertRequest;
import com.google.cloud.bigquery.InsertAllRequest;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.Test;

public class DeltaLayerBqInsertGeneratorTest {

  @Test
  public void generateSomeInserts() {
    ClassPathResourceFetcher classPathResourceFetcher =
        new ClassPathResourceFetcher("single_point_correction.json");
    InsertRequest insertRequest =
        GsonConverter.convertFromClass(
            new String(classPathResourceFetcher.fetchResourceBytes()), InsertRequest.class);
    OffsetDateTime insertTimeStamp = OffsetDateTime.of(LocalDateTime.now(), ZoneOffset.UTC);
    List<InsertAllRequest.RowToInsert> inserts =
        new DeltaLayerBqInsertGenerator().getInserts(insertRequest.getInserts(), insertTimeStamp);
    assertEquals("Should have created one insert", 1, inserts.size());
    assertEquals(
        "Insert timestamp is not what was expected",
        "2021-07-23T14:23:33.060729Z",
        insertRequest.getInsertTimestamp().toString());
  }
}
