package bio.terra.cloudfiletodatastore.deltalayer;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

import bio.terra.cloudfiletodatastore.deltalayer.model.json.PointCorrectionRequest;
import bio.terra.cloudfunctions.common.GsonWrapper;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.Test;

public class DeltaLayerBqInsertGeneratorTest {

  private List<InsertData> getInsertDataFromFile(String file) {
    ClassPathResourceFetcher classPathResourceFetcher = new ClassPathResourceFetcher(file);
    PointCorrectionRequest pointCorrectionRequest =
        GsonWrapper.convertFromClass(
            new String(classPathResourceFetcher.fetchResourceBytes()),
            PointCorrectionRequest.class);
    return new DeltaLayerBqInsertGenerator()
        .getInserts(
            pointCorrectionRequest.getInserts(),
            pointCorrectionRequest.getInsertTimestamp(),
            pointCorrectionRequest.getInsertId());
  }

  @Test
  public void testStringInsert() {
    List<InsertData> inserts = getInsertDataFromFile("string_point_correction.json");
    assertEquals("Should have created one insert", 1, inserts.size());
    assertEquals("hello there", inserts.get(0).getData().get("str_val"));
    assertEquals("2021-07-23T14:23:33.060729Z", inserts.get(0).getData().get("updated_at"));
  }

  @Test
  public void testBooleanInsert() {
    List<InsertData> inserts = getInsertDataFromFile("bool_point_correction.json");
    assertEquals("Should have created one insert", 1, inserts.size());
    assertEquals(false, inserts.get(0).getData().get("bool_val"));
  }

  @Test
  public void testTsInsert() {
    List<InsertData> inserts = getInsertDataFromFile("ts_point_correction.json");
    assertEquals("Should have created one insert", 1, inserts.size());
    assertEquals("2021-08-23T14:23:33.060729Z", inserts.get(0).getData().get("ts_val"));
  }

  @Test
  public void testDateInsert() {
    List<InsertData> inserts = getInsertDataFromFile("date_point_correction.json");
    assertEquals("Should have created one insert", 1, inserts.size());
    assertEquals("2021-09-23", inserts.get(0).getData().get("date_val"));
  }

  @Test
  public void getTypedValuesForInsert() {
    DeltaLayerBqInsertGenerator insertGenerator = new DeltaLayerBqInsertGenerator();
    assertTrue(insertGenerator.getTypedValue("a string") instanceof String);
    assertTrue(insertGenerator.getTypedValue(111) instanceof Long);
    assertTrue(insertGenerator.getTypedValue(111.22) instanceof Double);
    assertTrue(insertGenerator.getTypedValue(true) instanceof Boolean);
    assertTrue(insertGenerator.getTypedValue("") instanceof String);
    assertTrue(insertGenerator.getTypedValue("2011-12-03T10:15:30Z") instanceof OffsetDateTime);
    assertTrue(insertGenerator.getTypedValue("2011-12-03") instanceof LocalDate);
  }

  @Test
  public void testDateValues() {
    DeltaLayerBqInsertGenerator insertGenerator = new DeltaLayerBqInsertGenerator();
    assertFalse(insertGenerator.isValidTs("11/12/2021"));
    assertFalse(insertGenerator.isValidTs("garbage"));
    assertTrue(insertGenerator.isValidTs("2019-12-03T10:15:30Z"));
  }
}
