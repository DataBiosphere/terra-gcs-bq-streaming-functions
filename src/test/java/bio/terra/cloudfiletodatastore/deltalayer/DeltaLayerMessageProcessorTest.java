package bio.terra.cloudfiletodatastore.deltalayer;

import static org.mockito.ArgumentMatchers.any;

import bio.terra.cloudfiletodatastore.FileMessage;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.InsertAllResponse;
import java.util.Date;
import org.junit.Test;
import org.mockito.Mockito;

public class DeltaLayerMessageProcessorTest {

  @Test
  public void sunnyDay() {
    FileMessage message = new FileMessage("myUrl", "theBucker", 100, new Date());
    BigQuery bqMock = Mockito.mock(BigQuery.class);
    Mockito.when(bqMock.insertAll(any())).thenReturn(Mockito.mock(InsertAllResponse.class));
    DeltaLayerMessageProcessor messageProcessor =
        new DeltaLayerMessageProcessor(
            message, new ClassPathResourceFetcher("single_point_correction.json"), bqMock);
    messageProcessor.processMessage();
    Mockito.verify(bqMock, Mockito.times(1)).insertAll(any());
  }


}
