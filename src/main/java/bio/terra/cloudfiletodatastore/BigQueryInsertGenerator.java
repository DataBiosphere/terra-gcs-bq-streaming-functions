package bio.terra.cloudfiletodatastore;

import bio.terra.cloudfiletodatastore.deltalayer.model.BqInserts;
import java.util.List;

public interface BigQueryInsertGenerator<T> {

  List<BqInserts> getInserts(T toConvert);
}
