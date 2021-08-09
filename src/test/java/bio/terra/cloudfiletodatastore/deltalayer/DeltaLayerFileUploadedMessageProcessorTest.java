package bio.terra.cloudfiletodatastore.deltalayer;

import static org.mockito.ArgumentMatchers.any;

import bio.terra.cloudfiletodatastore.FileUploadedMessage;
import com.google.cloud.bigquery.*;
import java.time.OffsetDateTime;
import org.junit.Test;
import org.mockito.Mockito;

public class DeltaLayerFileUploadedMessageProcessorTest {

  @Test
  public void sunnyDay() throws InterruptedException {
    FileUploadedMessage message =
        new FileUploadedMessage("myUrl", "bucket", 100, OffsetDateTime.now(), "application/json");
    BigQuery bqMock = Mockito.mock(BigQuery.class);
    TableResult trMock = Mockito.mock(TableResult.class);
    Mockito.when(trMock.getTotalRows()).thenReturn(1L);
    Mockito.when(bqMock.query(any())).thenReturn(trMock);
    Mockito.when(bqMock.insertAll(any())).thenReturn(Mockito.mock(InsertAllResponse.class));
    DeltaLayerFileUploadedMessageProcessor messageProcessor =
        new DeltaLayerFileUploadedMessageProcessor(
            message,
            new DeltaLayerBQSQLWriter(),
            bqMock,
            new ClassPathResourceFetcher("string_point_correction.json"));
    messageProcessor.processMessage();
    Mockito.verify(bqMock, Mockito.times(1)).insertAll(any());
  }
}
