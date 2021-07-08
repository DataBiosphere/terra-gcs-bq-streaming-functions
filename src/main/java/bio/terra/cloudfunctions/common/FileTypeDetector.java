package bio.terra.cloudfunctions.common;

public class FileTypeDetector<T extends ContentHandler> {
  private T contentHandler;

  public FileTypeDetector(T contentHandler) {
    this.contentHandler = contentHandler;
  }
}
