package bio.terra.cloudfiletodatastore.deltalayer;

import static junit.framework.TestCase.*;

import bio.terra.cloudevents.GCSEvent;
import bio.terra.cloudfiletodatastore.ResourceFetcher;
import bio.terra.cloudfiletodatastore.deltalayer.model.json.PointCorrectionDestination;
import bio.terra.cloudfiletodatastore.deltalayer.model.json.PointCorrectionRequest;
import bio.terra.cloudfunctions.common.GsonWrapper;
import com.google.gson.JsonSyntaxException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import org.junit.Test;

public class GsonParsingTest {

  @Test
  public void parsePointCorrection() {
    ResourceFetcher resourceFetcher = new ClassPathResourceFetcher("string_point_correction.json");
    PointCorrectionRequest pointCorrectionRequest =
        GsonWrapper.convertFromClass(
            new String(resourceFetcher.fetchResourceBytes()), PointCorrectionRequest.class);
    assertEquals("Should only be one insert", 1, pointCorrectionRequest.getInserts().size());
    assertEquals(
        "Destination should match what's in string_point_correction.json",
        new PointCorrectionDestination(
            "point_correction",
            UUID.fromString("fe2dfaa7-e3a8-4725-b652-499f4eb2c88d"),
            "delta-layer-schema-tests"),
        pointCorrectionRequest.getDestination());
    assertNotNull(pointCorrectionRequest.getInsertTimestamp());
    assertEquals(
        OffsetDateTime.of(2021, 7, 23, 14, 23, 33, 60729000, ZoneOffset.UTC),
        pointCorrectionRequest.getInsertTimestamp());
  }

  @Test
  public void parseDatePointCorrection() {
    ResourceFetcher resourceFetcher = new ClassPathResourceFetcher("ts_point_correction.json");
    PointCorrectionRequest pointCorrectionRequest =
        GsonWrapper.convertFromClass(
            new String(resourceFetcher.fetchResourceBytes()), PointCorrectionRequest.class);
    Object typedValue =
        new DeltaLayerBqInsertGenerator()
            .getTypedValue(pointCorrectionRequest.getInserts().get(0).getValue());
    assertTrue(typedValue instanceof OffsetDateTime);
  }

  @Test
  public void parseLongPointCorrection() {
    ResourceFetcher resourceFetcher = new ClassPathResourceFetcher("long_point_correction.json");
    PointCorrectionRequest pointCorrectionRequest =
        GsonWrapper.convertFromClass(
            new String(resourceFetcher.fetchResourceBytes()), PointCorrectionRequest.class);
    assertEquals(
        UUID.fromString("d3b7b01f-952e-4d5e-b4c2-548c21409da0"),
        pointCorrectionRequest.getInsertId());
    Object typedValue =
        new DeltaLayerBqInsertGenerator()
            .getTypedValue(pointCorrectionRequest.getInserts().get(0).getValue());
    assertTrue(typedValue instanceof Long);
    assertEquals(11L, typedValue);
  }

  @Test
  public void parseDoublePointCorrection() {
    ResourceFetcher resourceFetcher = new ClassPathResourceFetcher("double_point_correction.json");
    PointCorrectionRequest pointCorrectionRequest =
        GsonWrapper.convertFromClass(
            new String(resourceFetcher.fetchResourceBytes()), PointCorrectionRequest.class);
    Object typedValue =
        new DeltaLayerBqInsertGenerator()
            .getTypedValue(pointCorrectionRequest.getInserts().get(0).getValue());
    assertTrue(typedValue instanceof Double);
    assertEquals(3.14, typedValue);
  }

  @Test
  public void parseBooleanPointCorrection() {
    ResourceFetcher resourceFetcher = new ClassPathResourceFetcher("bool_point_correction.json");
    PointCorrectionRequest pointCorrectionRequest =
        GsonWrapper.convertFromClass(
            new String(resourceFetcher.fetchResourceBytes()), PointCorrectionRequest.class);
    Object typedValue =
        new DeltaLayerBqInsertGenerator()
            .getTypedValue(pointCorrectionRequest.getInserts().get(0).getValue());
    assertTrue(typedValue instanceof Boolean);
    assertEquals(false, typedValue);
  }

  @Test(expected = JsonSyntaxException.class)
  public void parseMalformedJson() {
    ResourceFetcher resourceFetcher = new ClassPathResourceFetcher("bad_json.json");
    GsonWrapper.convertFromClass(
        new String(resourceFetcher.fetchResourceBytes()), PointCorrectionRequest.class);
  }

  @Test
  public void parseToGcsEvent() {
    ResourceFetcher resourceFetcher = new ClassPathResourceFetcher("gcsevent.json");
    GCSEvent gcsEvent =
        GsonWrapper.convertFromClass(
            new String(resourceFetcher.fetchResourceBytes()), GCSEvent.class);
    assertNotNull(gcsEvent.getId());
    assertNotNull(gcsEvent.getTimeCreated());
  }
}
