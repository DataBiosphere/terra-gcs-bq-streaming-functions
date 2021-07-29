package bio.terra.cloudfiletodatastore.deltalayer;

import bio.terra.cloudfiletodatastore.*;
import bio.terra.cloudfiletodatastore.deltalayer.model.json.InsertDestination;
import bio.terra.cloudfiletodatastore.deltalayer.model.json.InsertRequest;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.InsertAllRequest;
import com.google.common.annotations.VisibleForTesting;
import java.util.List;
import java.util.logging.Logger;

public class DeltaLayerMessageProcessor extends MessageProcessor {

  private final ResourceFetcher resourceFetcher;

  private final DeltaLayerBQWriter bqWriter;

  private static final Logger logger = Logger.getLogger(DeltaLayerMessageProcessor.class.getName());

  public DeltaLayerMessageProcessor(FileMessage message) {
    super(message);
    this.resourceFetcher = new GcsFileFetcher(message.getSourceBucket(), message.getResourceUrl());
    this.bqWriter = new DeltaLayerBigQueryWriter();
  }

  @VisibleForTesting
  DeltaLayerMessageProcessor(FileMessage message, ResourceFetcher rf, DeltaLayerBQWriter bqWriter) {
    super(message);
    this.resourceFetcher = rf;
    this.bqWriter = bqWriter;
  }

  @Override
  public void processMessage() {
    byte[] resourceBytes = resourceFetcher.fetchResourceBytes();
    InsertRequest insertRequest =
        GsonConverter.convertFromClass(new String(resourceBytes), InsertRequest.class);
    logger.info(
        String.format("Length of point corrections is %s", insertRequest.getInserts().size()));
    DeltaLayerBqInsertGenerator bqGenerator = new DeltaLayerBqInsertGenerator();
    List<InsertAllRequest.RowToInsert> inserts = bqGenerator.getInserts(insertRequest.getInserts());
    logger.info(String.format("Length of inserts is %s", inserts.size()));
    InsertDestination destination = insertRequest.getDestination();
    DeltaLayerBigQueryWriter deltaLayerBigQueryWriter = new DeltaLayerBigQueryWriter();
    deltaLayerBigQueryWriter.insertRows(inserts, destination.getBqDataset(), destination.getDatasetProject());
  }
}
