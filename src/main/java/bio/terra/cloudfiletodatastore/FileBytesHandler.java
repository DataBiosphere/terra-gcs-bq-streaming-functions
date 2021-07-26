package bio.terra.cloudfiletodatastore;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

/**
 * Implementations should convert byte[] to specified type, the default assumes the bytes provided
 * are a JSON string and the generic type is a Gson friendly class or collection.
 */
public interface FileBytesHandler<T> {

  default T convertBytes(byte[] toConvert) {
    Type targetClassType = new TypeToken<T>() {}.getType();
    return new Gson().fromJson(new String(toConvert), targetClassType);
  }
}
