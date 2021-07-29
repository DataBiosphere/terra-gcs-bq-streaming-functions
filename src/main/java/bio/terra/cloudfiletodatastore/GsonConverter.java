package bio.terra.cloudfiletodatastore;

import bio.terra.cloudfunctions.common.GsonWrapper;

public class GsonConverter {

  public static <T> T convertFromClass(String s, Class<T> classOfT) {
    return GsonWrapper.getInstance().fromJson(s, classOfT);
  }
}
