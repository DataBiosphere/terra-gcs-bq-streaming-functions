package bio.terra.cloudfiletodatastore.deltalayer;

import bio.terra.cloudfiletodatastore.ResourceFetcher;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.util.logging.Logger;

public class GcsFileFetcher implements ResourceFetcher {

  private final String bucket;
  private final String name;

  private static final Logger logger = Logger.getLogger(GcsFileFetcher.class.getName());

  public GcsFileFetcher(String bucket, String name) {
    this.bucket = bucket;
    this.name = name;
  }

  @Override
  public byte[] fetchResourceBytes() {
    // TODO: figure out best practices for retries, validating download
    Storage service = StorageOptions.newBuilder().build().getService();
    Blob blob = service.get(BlobId.of(bucket, name));
    byte[] content = blob.getContent();
    logger.info(String.format("Bytes length is %s", content.length));
    return content;
  }
}
