package bio.terra.cloudfunctions.common;

import org.apache.commons.lang3.StringUtils;

public class EventWrapper<T> {
  /**
   * Parse an event String and returns the event represented by generic event type E.
   *
   * @param json the event String
   * @param type the type of the event
   * @return object representing event type E
   */
  public static <E> E parseEvent(String json, Class<E> type) {
    assert (StringUtils.isNotBlank(json));
    return GsonWrapper.getInstance().fromJson(json, type);
  }
}
