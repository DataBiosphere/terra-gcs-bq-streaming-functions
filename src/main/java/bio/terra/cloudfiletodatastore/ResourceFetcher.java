package bio.terra.cloudfiletodatastore;

import java.io.InputStream;

/**
 * Implementations should include the logic to fetch a remote resource such as a json file or
 * archive of json files.
 */
public interface ResourceFetcher {

  default byte[] fetchResourceBytes() {
    throw new UnsupportedOperationException();
  }

  default InputStream fetchResourceStream() {
    throw new UnsupportedOperationException();
  }
}
