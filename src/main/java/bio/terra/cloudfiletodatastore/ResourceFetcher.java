package bio.terra.cloudfiletodatastore;

import java.io.InputStream;

/**
 * Implementations should include the logic to fetch a remote resource such as a json file or
 * archive of json files and return that resource's bytes. This approach makes for easier coding,
 * but it implies pulling all of the resource's contents into memory--something we may need to
 * revisit.
 */
public interface ResourceFetcher {

  default byte[] fetchResourceBytes() {
    throw new UnsupportedOperationException();
  }

  default InputStream fetchResourceStream() {
    throw new UnsupportedOperationException();
  }
}
