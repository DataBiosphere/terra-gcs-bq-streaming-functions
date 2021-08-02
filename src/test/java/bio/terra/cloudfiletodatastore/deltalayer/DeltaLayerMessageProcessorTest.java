package bio.terra.cloudfiletodatastore.deltalayer;

import static org.mockito.ArgumentMatchers.any;

import bio.terra.cloudfiletodatastore.FileMessage;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.InsertAllResponse;
import com.google.cloud.bigquery.TableResult;
import java.time.OffsetDateTime;
import org.junit.Test;
import org.mockito.Mockito;

public class DeltaLayerMessageProcessorTest {

  @Test
  public void sunnyDay() throws InterruptedException {
    FileMessage message =
        new FileMessage("myUrl", "bucket", 100, OffsetDateTime.now(), "application/json");
    BigQuery bqMock = Mockito.mock(BigQuery.class);
    TableResult trMock = Mockito.mock(TableResult.class);
    Mockito.when(trMock.getTotalRows()).thenReturn(1L);
    Mockito.when(bqMock.query(any())).thenReturn(trMock);
    Mockito.when(bqMock.insertAll(any())).thenReturn(Mockito.mock(InsertAllResponse.class));
    DeltaLayerMessageProcessor messageProcessor =
        new DeltaLayerMessageProcessor(
            message, new ClassPathResourceFetcher("single_point_correction.json"), bqMock);
    messageProcessor.processMessage();
    Mockito.verify(bqMock, Mockito.times(1)).insertAll(any());
  }
}
