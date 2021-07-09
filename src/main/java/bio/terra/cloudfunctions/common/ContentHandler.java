package bio.terra.cloudfunctions.common;

import com.google.events.cloud.storage.v1.StorageObjectData;

public abstract class ContentHandler<E extends StorageObjectData> extends FileTypeDetector<E> {
  public ContentHandler() {}

  public void translate() throws Exception {
    throw new UnsupportedOperationException("translate method must be overridden by sub-classes");
  }

  @Override
  public void handleMediaType() throws Exception {
    super.handleMediaType();
    translate();
  }

  public void insert() throws Exception {
    throw new UnsupportedOperationException("insert method must be overridden by sub-classes");
  }
}
