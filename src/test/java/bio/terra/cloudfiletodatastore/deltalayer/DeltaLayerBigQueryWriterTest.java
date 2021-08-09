package bio.terra.cloudfiletodatastore.deltalayer;

import static org.mockito.ArgumentMatchers.any;

import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.InsertAllResponse;
import com.google.cloud.bigquery.TableInfo;
import com.google.cloud.bigquery.TableResult;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.mockito.Mockito;

public class DeltaLayerBigQueryWriterTest {

  @Test
  public void eavTableMissing() throws InterruptedException {
    DeltaLayerBigQueryWriter writerToTest = new DeltaLayerBQSQLWriter();
    BigQuery bqMock = Mockito.mock(BigQuery.class);
    TableResult trMock = Mockito.mock(TableResult.class);
    Mockito.when(trMock.getTotalRows()).thenReturn(0L);
    Mockito.when(bqMock.query(any())).thenReturn(trMock);
    Mockito.when(bqMock.insertAll(any())).thenReturn(Mockito.mock(InsertAllResponse.class));
    writerToTest.insertRows(List.of((Map.of("foo", "bar"))), "point_correction", "project", bqMock);
    Mockito.verify(bqMock, Mockito.times(1)).query(any());
    Mockito.verify(bqMock, Mockito.times(1)).create((TableInfo) any());
    Mockito.verify(bqMock, Mockito.times(1)).insertAll(any());
  }
}
