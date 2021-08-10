package bio.terra.cloudfiletodatastore;

/**
 * Implementations should include the logic to fetch a remote resource such as a json file or
 * archive of json files.
 */
public interface ResourceFetcher {

  byte[] fetchResourceBytes();
}
