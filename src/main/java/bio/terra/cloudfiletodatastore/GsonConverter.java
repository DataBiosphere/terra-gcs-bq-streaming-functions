package bio.terra.cloudfiletodatastore;

import bio.terra.cloudfunctions.common.GsonWrapper;
import com.google.gson.reflect.TypeToken;
import java.util.List;

public class GsonConverter {

  public static <T> T convertFromClass(String s, Class<T> classOfT) {
    return GsonWrapper.getInstance().fromJson(s, classOfT);
  }

  public static <T> List<T> convertToListofClass(byte[] bytes, TypeToken listTypeToken) {
    return GsonWrapper.getInstance().fromJson(new String(bytes), listTypeToken.getType());
  }
}
