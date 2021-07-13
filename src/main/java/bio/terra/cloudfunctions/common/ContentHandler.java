package bio.terra.cloudfunctions.common;

import com.google.events.cloud.storage.v1.StorageObjectData;

public abstract class ContentHandler<E extends StorageObjectData> extends FileTypeDetector<E> {
  public ContentHandler() {}
  /**
   * The translate method is to be overridden in inherited class that will transform the raw
   * dataStream into domain-specific format before further processing.
   */
  public void translate() throws Exception {
    throw new UnsupportedOperationException("translate method must be overridden by sub-classes");
  }
  /**
   * This handleMediaType method calls the base class method then delegates to the domain-specific
   * translator.
   */
  @Override
  public void handleMediaType() throws Exception {
    super.handleMediaType();
    translate();
  }
  /** This insert method is used for inserting the translated data into BigQuery. */
  public void insert(String table, byte[] data) throws Exception {
    throw new UnsupportedOperationException("insert method must be overridden by sub-classes");
  }
}
