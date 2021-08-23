package bio.terra.cloudfunctions.common;

import com.google.common.net.MediaType;
import java.util.Objects;

public class MediaTypeWrapper {
  // Workaround for Mime types not found in Guava MediaType
  // See issues https://github.com/google/guava/issues/3946
  // and https://github.com/google/guava/issues/3414.
  private static final MediaType APPLICATION_GZIP = MediaType.create(MediaType.GZIP.type(), "gzip");
  private static final MediaType APPLICATION_JSON =
      MediaType.create(MediaType.JSON_UTF_8.type(), "json");

  private MediaType mediaType;

  /**
   * MediaTypeWrapper constructor.
   *
   * @param input the Content-Type represented by the wrapper object, e.g. application/json
   * @return MediaTypeWrapper object
   */
  public MediaTypeWrapper(String input) {
    this.mediaType = MediaType.parse(input);
  }

  /**
   * Check whether the MediaType represented by the wrapper object is equivalent to the provided
   * parameter.
   *
   * @param mediaTypeRange Guava MediaType to compare to
   * @return true/false
   */
  public boolean is(MediaType mediaTypeRange) {
    return mediaType.is(Objects.requireNonNull(mediaTypeRange));
  }
  /**
   * Check whether the MediaType represented by the wrapper object is equivalent to GZIP type.
   *
   * @return true/false
   */
  public boolean isApplicationGzip() {
    return is(MediaType.GZIP) || is(MediaTypeWrapper.APPLICATION_GZIP);
  }
  /**
   * Check whether the MediaType represented by the wrapper object is equivalent to JSON type.
   *
   * @return true/false
   */
  public boolean isApplicationJson() {
    return is(MediaType.JSON_UTF_8) || is(MediaTypeWrapper.APPLICATION_JSON);
  }
}
