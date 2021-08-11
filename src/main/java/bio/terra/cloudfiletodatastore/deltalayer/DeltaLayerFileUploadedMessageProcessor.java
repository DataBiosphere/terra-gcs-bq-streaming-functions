package bio.terra.cloudfiletodatastore.deltalayer;

import bio.terra.cloudfiletodatastore.*;
import bio.terra.cloudfiletodatastore.deltalayer.model.json.PointCorrectionDestination;
import bio.terra.cloudfiletodatastore.deltalayer.model.json.PointCorrectionRequest;
import bio.terra.cloudfunctions.common.GsonWrapper;
import com.google.cloud.bigquery.BigQuery;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Fetches the json file referenced in the {@link FileUploadedMessage} from cloud storage, parses
 * that file into Java using Gson, inserts the requisite data into BigQuery
 */
public class DeltaLayerFileUploadedMessageProcessor extends MessageProcessor {

  private final ResourceFetcher resourceFetcher;

  private static final Logger logger =
      Logger.getLogger(DeltaLayerFileUploadedMessageProcessor.class.getName());

  private final DeltaLayerBigQueryWriter deltaLayerBigQueryWriter;

  private final BigQuery bigQuery;

  public DeltaLayerFileUploadedMessageProcessor(
      FileUploadedMessage message,
      DeltaLayerBigQueryWriter dlWriter,
      BigQuery bigQuery,
      ResourceFetcher rf) {
    super(message);
    this.resourceFetcher = rf;
    deltaLayerBigQueryWriter = dlWriter;
    this.bigQuery = bigQuery;
  }

  @Override
  public void processMessage() {
    byte[] resourceBytes = resourceFetcher.fetchResourceBytes();
    PointCorrectionRequest pointCorrectionRequest =
        GsonWrapper.convertFromClass(new String(resourceBytes), PointCorrectionRequest.class);
    logger.info(
        String.format(
            "Length of deserialized point corrections is %s",
            pointCorrectionRequest.getInserts().size()));
    DeltaLayerBqInsertGenerator bqGenerator = new DeltaLayerBqInsertGenerator();
    List<Map<String, Object>> inserts =
        bqGenerator.getInserts(
            pointCorrectionRequest.getInserts(), pointCorrectionRequest.getInsertTimestamp());
    logger.info(String.format("Length of generated bq inserts is %s", inserts.size()));
    PointCorrectionDestination destination = pointCorrectionRequest.getDestination();
    deltaLayerBigQueryWriter.insertRows(
        inserts, destination.getBqDataset(), destination.getDatasetProject(), bigQuery);
  }
}
