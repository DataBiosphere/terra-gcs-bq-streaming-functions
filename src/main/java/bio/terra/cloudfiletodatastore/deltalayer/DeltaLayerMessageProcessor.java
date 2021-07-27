package bio.terra.cloudfiletodatastore.deltalayer;

import bio.terra.cloudfiletodatastore.*;
import bio.terra.cloudfiletodatastore.deltalayer.model.BqInserts;
import bio.terra.cloudfiletodatastore.deltalayer.model.PointCorrection;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class DeltaLayerMessageProcessor extends MessageProcessor {
  private final BigQueryInsertGenerator<List<PointCorrection>> bqGenerator;
  private final ResourceFetcher resourceFetcher;
  private final BigQueryWriter bigQueryWriter;

  private static final Logger logger = Logger.getLogger(DeltaLayerMessageProcessor.class.getName());

  public DeltaLayerMessageProcessor(FileMessage message) {
    super(message);
    this.bqGenerator = new DeltaLayerBqGenerator();
    this.resourceFetcher = new GcsFileFetcher(message.getSourceBucket(), message.getResourceUrl());
    this.bigQueryWriter = new BigQueryWriter();
  }

  @Override
  public void processMessage() {
    byte[] resourceBytes = resourceFetcher.fetchResource();
    List<PointCorrection> pointCorrections = GsonConverter.convertToListofClass(resourceBytes, new TypeToken<ArrayList<PointCorrection>>() {});
    logger.info(String.format("Length of point corrections is %s", pointCorrections.size()));
    List<BqInserts> inserts = bqGenerator.getInserts(pointCorrections);
    logger.info(String.format("Length of inserts is %s", inserts.size()));
    bigQueryWriter.insertRows(inserts);
  }

}
