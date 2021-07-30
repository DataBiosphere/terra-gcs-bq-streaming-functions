package bio.terra.cloudfiletodatastore.deltalayer;

import static org.mockito.ArgumentMatchers.any;

import bio.terra.cloudfiletodatastore.FileMessage;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.InsertAllResponse;
import java.time.OffsetDateTime;
import org.junit.Test;
import org.mockito.Mockito;

public class DeltaLayerMessageProcessorTest {

  @Test
  public void sunnyDay() {
    FileMessage message = new FileMessage("myUrl", "bucket", 100, OffsetDateTime.now(), "json");
    BigQuery bqMock = Mockito.mock(BigQuery.class);
    Mockito.when(bqMock.insertAll(any())).thenReturn(Mockito.mock(InsertAllResponse.class));
    DeltaLayerMessageProcessor messageProcessor =
        new DeltaLayerMessageProcessor(
            message, new ClassPathResourceFetcher("single_point_correction.json"), bqMock);
    messageProcessor.processMessage();
    Mockito.verify(bqMock, Mockito.times(1)).insertAll(any());
  }
}
