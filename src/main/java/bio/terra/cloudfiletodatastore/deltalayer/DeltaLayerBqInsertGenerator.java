package bio.terra.cloudfiletodatastore.deltalayer;

import bio.terra.cloudfiletodatastore.deltalayer.model.json.PointCorrectionOperation;
import com.google.common.annotations.VisibleForTesting;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import org.apache.commons.validator.GenericValidator;

/**
 * Responsible for translating bio.terra.cloudfiletodatastore.model.json.* instances into {@link
 * com.google.cloud.bigquery.InsertAllRequest.RowToInsert}
 */
public class DeltaLayerBqInsertGenerator {

  private Map<Class, String> typeColumns =
      Map.of(
          String.class,
          "str_val",
          Long.class,
          "int_val",
          Double.class,
          "float_val",
          Boolean.class,
          "bool_val",
          OffsetDateTime.class,
          "ts_val",
          LocalDate.class,
          "date_val");

  public List<Map<String, Object>> getInserts(
      List<PointCorrectionOperation> toConvert, OffsetDateTime insertTimeStamp) {
    List<Map<String, Object>> inserts = new ArrayList<>();
    for (PointCorrectionOperation insert : toConvert) {
      Map<String, Object> data = new HashMap<>();
      data.put("datarepo_row_id", insert.getDatarepoRowId().toString());
      data.put("attribute_name", insert.getName());
      data.put("updated_at", insertTimeStamp.toString());
      data.put(
          getTargetColumn(insert.getValue()),
          serializeTypedValueForBq(getTypedValue(insert.getValue())));
      inserts.add(data);
    }
    return inserts;
  }

  @VisibleForTesting
  Object getTypedValue(Object value) {
    // order matters, if we put double before long, non-floating point values
    // will unnecessarily be represented as Doubles, String should come last
    String strval = value.toString();
    // gson will parse 11 as 11.0
    // https://stackoverflow.com/questions/45734769/why-does-gson-parse-an-integer-as-a-double
    if (strval.endsWith(".0") || GenericValidator.isLong(strval)) {
      return Long.valueOf(strval.substring(0, strval.length() - 2));
    }
    if (GenericValidator.isDouble(strval)) {
      return Double.valueOf(strval);
    }
    if ("false".equals(strval.toLowerCase(Locale.ROOT))
        || "true".equals(strval.toLowerCase(Locale.ROOT))) {
      return Boolean.valueOf(strval);
    }
    if (isValidTs(strval)) {
      return OffsetDateTime.parse(strval);
    }
    if (isValidDate(strval)) {
      return LocalDate.parse(strval);
    }
    return strval;
  }

  private boolean isValidDate(String strval) {
    try {
      LocalDate.parse(strval);
      return true;
    } catch (DateTimeParseException e) {
      return false;
    }
  }

  private Object serializeTypedValueForBq(Object val) {
    if (val instanceof OffsetDateTime || val instanceof LocalDate) {
      return val.toString();
    }
    return val;
  }

  @VisibleForTesting
  boolean isValidTs(String strval) {
    try {
      // https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#ISO_OFFSET_DATE_TIME
      OffsetDateTime.parse(strval);
      return true;
    } catch (DateTimeParseException e) {
      return false;
    }
  }

  private String getTargetColumn(Object value) {
    Object typedValue = getTypedValue(value);
    return typeColumns.get(typedValue.getClass());
  }
}
