package bio.terra.cloudfunctions.common;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Map;

public class GsonWrapper {
  private GsonWrapper() {}

  // Provide a thread-safe Gson singleton (Bill Pugh)
  private static class GsonSingleton {
    private static final Gson _instance =
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
  }
  /**
   * Returns a Gson instance capable of parsing a string that represents OffsetDateTime object.
   *
   * @return Gson object
   */
  public static Gson getInstance() {
    return GsonSingleton._instance;
  }

  /**
   * @param s json string
   * @param classOfT the class to cast the json string
   * @param <T> the return type
   * @return
   * @throws JsonSyntaxException when something goes wrong
   */
  public static <T> T convertFromClass(String s, Class<T> classOfT) throws JsonSyntaxException {
    return getInstance().fromJson(s, classOfT);
  }

  /**
   * @param j an JsonObject object
   * @param classOfT the class to cast the json object
   * @param <T> the return type
   * @return
   * @throws JsonSyntaxException when something goes wrong
   */
  public static <T> T convertFromClass(JsonObject j, Class<T> classOfT) throws JsonSyntaxException {
    return getInstance().fromJson(j, classOfT);
  }

  /**
   * @param obj a json object represented as a map
   * @param classOfT the class to cast the json object
   * @param <T> the return type
   * @return
   * @throws JsonSyntaxException when something goes wrong
   */
  public static <T> T convertFromClass(Map<?, ?> obj, Class<T> classOfT)
      throws JsonSyntaxException {
    return convertFromClass(getInstance().toJsonTree(obj).getAsJsonObject(), classOfT);
  }
}
