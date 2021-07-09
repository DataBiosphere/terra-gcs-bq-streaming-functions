package bio.terra.cloudfunctions.common;

import org.apache.commons.lang3.StringUtils;

public class EventWrapper<T> {
  public static <T> T parseEvent(String json, Class<T> type) {
    assert (StringUtils.isNotBlank(json));
    return GsonWrapper.getInstance().fromJson(json, type);
  }
}
