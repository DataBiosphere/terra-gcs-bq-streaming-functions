package bio.terra.cloudfiletodatastore.deltalayer;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.ArgumentMatchers.any;

import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.InsertAllResponse;
import com.google.cloud.bigquery.TableInfo;
import com.google.cloud.bigquery.TableResult;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.mockito.Mockito;

public class DeltaLayerBQSqlWriterTest {

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

  @Test
  public void eavTablePresent() throws InterruptedException {
    DeltaLayerBigQueryWriter writerToTest = new DeltaLayerBQSQLWriter();
    BigQuery bqMock = Mockito.mock(BigQuery.class);
    TableResult trMock = Mockito.mock(TableResult.class);
    Mockito.when(trMock.getTotalRows()).thenReturn(1L);
    Mockito.when(bqMock.query(any())).thenReturn(trMock);
    Mockito.when(bqMock.insertAll(any())).thenReturn(Mockito.mock(InsertAllResponse.class));
    writerToTest.insertRows(List.of((Map.of("foo", "bar"))), "point_correction", "project", bqMock);
    Mockito.verify(bqMock, Mockito.times(1)).query(any());
    Mockito.verify(bqMock, Mockito.times(0)).create((TableInfo) any());
    Mockito.verify(bqMock, Mockito.times(1)).insertAll(any());
  }

  @Test(expected = IllegalArgumentException.class)
  public void detectMaliciousNames() {
    DeltaLayerBQSQLWriter deltaLayerBQSQLWriter = new DeltaLayerBQSQLWriter();
    deltaLayerBQSQLWriter.sanitizeName("general-dev-billing-account;drop all tables;");
  }

  @Test(expected = IllegalArgumentException.class)
  public void detectMaliciousNames2() {
    DeltaLayerBQSQLWriter deltaLayerBQSQLWriter = new DeltaLayerBQSQLWriter();
    deltaLayerBQSQLWriter.sanitizeName("--general-dev-billing-account");
  }

  @Test(expected = IllegalArgumentException.class)
  public void detectMaliciousNames3() {
    DeltaLayerBQSQLWriter deltaLayerBQSQLWriter = new DeltaLayerBQSQLWriter();
    deltaLayerBQSQLWriter.sanitizeName("*general-dev-billing-account");
  }

  @Test
  public void allowValidName() {
    DeltaLayerBQSQLWriter deltaLayerBQSQLWriter = new DeltaLayerBQSQLWriter();
    String sanitized = deltaLayerBQSQLWriter.sanitizeName("general-dev-billing-account");
    assertEquals("general-dev-billing-account", sanitized);
  }

  @Test
  public void allowValidName2() {
    DeltaLayerBQSQLWriter deltaLayerBQSQLWriter = new DeltaLayerBQSQLWriter();
    String sanitized =
        deltaLayerBQSQLWriter.sanitizeName(
            "deltalayer_forworkspace_8f080501_96c3_4aee_8a73_bfab347a2f65");
    assertEquals("deltalayer_forworkspace_8f080501_96c3_4aee_8a73_bfab347a2f65", sanitized);
  }
}
