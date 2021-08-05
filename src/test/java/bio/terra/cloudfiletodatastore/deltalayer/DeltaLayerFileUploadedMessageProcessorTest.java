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
            message, new ClassPathResourceFetcher("string_point_correction.json"), bqMock);
    messageProcessor.processMessage();
    Mockito.verify(bqMock, Mockito.times(1)).insertAll(any());
  }

  @Test
  public void invalidFileType() {
    FileUploadedMessage message =
        new FileUploadedMessage("myUrl", "bucket", 100, OffsetDateTime.now(), "application/xml");
    BigQuery bqMock = Mockito.mock(BigQuery.class);
    DeltaLayerFileUploadedMessageProcessor messageProcessor =
        new DeltaLayerFileUploadedMessageProcessor(message, null, bqMock);
    messageProcessor.processMessage();
    Mockito.verify(bqMock, Mockito.times(0)).insertAll(any());
  }
}
