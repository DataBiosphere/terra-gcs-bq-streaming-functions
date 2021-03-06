package bio.terra.cloudfiletodatastore;

/**
 * Subclasses encapsulate business logic and should not use cloud provider api classes, all the
 * necessary data should transferred from the cloud provider specific classes to the {@link
 * FileUploadedMessage} instance passed to the constructor.
 */
public abstract class MessageProcessor {

  protected final FileUploadedMessage message;

  public MessageProcessor(FileUploadedMessage message) {
    this.message = message;
  }

  /**
   * Very generic place for doing the business thing with the data passed in. We could enforce more
   * structure here, but I'm inclined to wait and see what processing patterns emerge.
   */
  public abstract void processMessage();
}
