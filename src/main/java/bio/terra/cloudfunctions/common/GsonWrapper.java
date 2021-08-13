package bio.terra.cloudfunctions.common;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

public class GsonWrapper {
  private static Gson _instance = initInstance();
  /**
   * Returns a Gson instance capable of parsing a string that represents OffsetDateTime object.
   *
   * @return Gson object
   */
  private static Gson initInstance() {
    if (_instance == null)
      _instance =
          new GsonBuilder()
              .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
              .registerTypeAdapter(
                  OffsetDateTime.class,
                  (JsonDeserializer<OffsetDateTime>)
                      (json, type, jsonDeserializationContext) -> {
                        try {
                          return ZonedDateTime.parse(json.getAsString()).toOffsetDateTime();
                        } catch (Exception e) {
                          return null;
                        }
                      })
              .create();
    return _instance;
  }

  public static Gson getInstance() {
    return _instance;
  }

  public static <T> T convertFromClass(String s, Class<T> classOfT) {
    return getInstance().fromJson(s, classOfT);
  }
}
