package bio.terra.cloudfunctions.common;

public abstract class ContentHandler {
  public ContentHandler() {}

  public void translate() throws Exception {
    throw new UnsupportedOperationException("translate method must be overridden by sub-classes");
  }
}
