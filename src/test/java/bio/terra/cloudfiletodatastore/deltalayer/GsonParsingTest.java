package bio.terra.cloudfiletodatastore.deltalayer;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

import bio.terra.cloudfiletodatastore.GsonConverter;
import bio.terra.cloudfiletodatastore.ResourceFetcher;
import bio.terra.cloudfiletodatastore.deltalayer.model.json.InsertDestination;
import bio.terra.cloudfiletodatastore.deltalayer.model.json.InsertRequest;
import com.google.gson.JsonSyntaxException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import org.junit.Test;

public class GsonParsingTest {

  @Test
  public void parsePointCorrection() {
    ResourceFetcher resourceFetcher = new ClassPathResourceFetcher("single_point_correction.json");
    InsertRequest insertRequest =
        GsonConverter.convertFromClass(
            new String(resourceFetcher.fetchResourceBytes()), InsertRequest.class);
    assertEquals("Should only be one insert", 1, insertRequest.getInserts().size());
    assertEquals(
        "Destination should match what's in single_point_correction.json",
        new InsertDestination(
            "point_correction",
            UUID.fromString("fe2dfaa7-e3a8-4725-b652-499f4eb2c88d"),
            "delta-layer-schema-tests"),
        insertRequest.getDestination());
    assertNotNull(insertRequest.getInsertTimestamp());
    assertEquals(
        OffsetDateTime.of(2021, 7, 23, 14, 23, 33, 60729000, ZoneOffset.UTC),
        insertRequest.getInsertTimestamp());
  }

  @Test(expected = JsonSyntaxException.class)
  public void parseMalformedJson() {
    ResourceFetcher resourceFetcher = new ClassPathResourceFetcher("bad_json.json");
    GsonConverter.convertFromClass(
        new String(resourceFetcher.fetchResourceBytes()), InsertRequest.class);
  }
}
