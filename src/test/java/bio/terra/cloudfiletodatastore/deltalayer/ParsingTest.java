package bio.terra.cloudfiletodatastore.deltalayer;

import bio.terra.cloudfiletodatastore.GsonConverter;
import bio.terra.cloudfiletodatastore.deltalayer.model.BqInserts;
import bio.terra.cloudfiletodatastore.deltalayer.model.PointCorrection;
import com.google.common.io.Files;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class ParsingTest {

  @Test
  public void testParsing() throws IOException {
    File file =
        new File(
            "/home/dhite/code-repos/broad/terra-gcs-bq-streaming-functions/src/test/resources/bio.terra/cloudfiletodatastore/single_point_correction.json");
    byte[] bytes = Files.toByteArray(file);
    List<PointCorrection> pointCorrections =
        GsonConverter.convertToListofClass(bytes, new TypeToken<ArrayList<PointCorrection>>() {});
    System.out.println(pointCorrections.get(0).getName());
    List<BqInserts> inserts = new DeltaLayerBqGenerator().getInserts(pointCorrections);
    System.out.println(inserts.get(0).getData().size());
  }
}
