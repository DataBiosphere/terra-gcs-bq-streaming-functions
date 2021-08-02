package bio.terra.cloudfiletodatastore.deltalayer;

import bio.terra.cloudfiletodatastore.*;
import bio.terra.cloudfiletodatastore.deltalayer.model.json.PointCorrectionDestination;
import bio.terra.cloudfiletodatastore.deltalayer.model.json.PointCorrectionRequest;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.InsertAllRequest;
import com.google.common.annotations.VisibleForTesting;
import java.util.List;
import java.util.logging.Logger;

public class DeltaLayerMessageProcessor extends MessageProcessor {

  private static final String EXPECTED_CONTENT_TYPE = "application/json";

  private final ResourceFetcher resourceFetcher;

  private BigQuery bqForTest;

  private static final Logger logger = Logger.getLogger(DeltaLayerMessageProcessor.class.getName());

  public DeltaLayerMessageProcessor(FileUploadedMessage message) {
    super(message);
    this.resourceFetcher = new GcsFileFetcher(message.getSourceBucket(), message.getResourceName());
  }

  @VisibleForTesting
  DeltaLayerMessageProcessor(FileUploadedMessage message, ResourceFetcher rf, BigQuery bq) {
    super(message);
    this.resourceFetcher = rf;
    this.bqForTest = bq;
  }

  @Override
  public void processMessage() {
    if (!EXPECTED_CONTENT_TYPE.equals(message.getContentType())) {
      logger.warning("Unexpected content type, concluding processing early");
      return;
    }
    byte[] resourceBytes = resourceFetcher.fetchResourceBytes();
    PointCorrectionRequest pointCorrectionRequest =
        GsonConverter.convertFromClass(new String(resourceBytes), PointCorrectionRequest.class);
    logger.info(
        String.format(
            "Length of deserialized point corrections is %s",
            pointCorrectionRequest.getInserts().size()));
    DeltaLayerBqInsertGenerator bqGenerator = new DeltaLayerBqInsertGenerator();
    List<InsertAllRequest.RowToInsert> inserts =
        bqGenerator.getInserts(
            pointCorrectionRequest.getInserts(), pointCorrectionRequest.getInsertTimestamp());
    logger.info(String.format("Length of generated bq inserts is %s", inserts.size()));
    PointCorrectionDestination destination = pointCorrectionRequest.getDestination();
    DeltaLayerBigQueryWriter deltaLayerBigQueryWriter = new DeltaLayerBigQueryWriter();
    if (null != bqForTest) {
      deltaLayerBigQueryWriter.insertRows(inserts, destination.getBqDataset(), bqForTest);
    } else {
      deltaLayerBigQueryWriter.insertRows(
          inserts,
          destination.getBqDataset(),
          BigQueryOptions.newBuilder()
              .setProjectId(destination.getDatasetProject())
              .build()
              .getService());
    }
  }
}
