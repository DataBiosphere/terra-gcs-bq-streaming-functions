package bio.terra.cloudfunctions.common;

import com.google.common.net.MediaType;

public class MediaTypeWrapper {
  // Workaround for Mime types not found in Guava MediaType
  public static final MediaType APPLICATION_GZIP = MediaType.create(MediaType.GZIP.type(), "gzip");
  public static final MediaType APPLICATION_JSON =
      MediaType.create(MediaType.JSON_UTF_8.type(), "json");

  private MediaType mediaType;

  public MediaTypeWrapper(String input) {
    this.mediaType = MediaType.parse(input);
  }

  public boolean is(MediaType mediaTypeRange) {
    assert (mediaTypeRange != null);
    return mediaType.is(mediaTypeRange);
  }

  public boolean isApplicationGzip() {
    return is(MediaType.GZIP) || is(MediaTypeWrapper.APPLICATION_GZIP);
  }

  public boolean isApplicationJson() {
    return is(MediaType.JSON_UTF_8) || is(MediaTypeWrapper.APPLICATION_JSON);
  }
}
