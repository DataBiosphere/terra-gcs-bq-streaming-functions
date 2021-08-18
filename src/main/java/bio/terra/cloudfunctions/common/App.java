package bio.terra.cloudfunctions.common;

import bio.terra.cloudfiletodatastore.FileUploadedMessage;

/**
 * This abstract class represents the business application that interprets CloudEvent messages.
 *
 * <p>Sub-classes overrides the process() method with business logic to achieve desired functional
 * goals.
 *
 * <p>Sub-classes can be integrated with a DI framework to deploy the business logic like as a
 * Service.
 */
public abstract class App {
  protected FileUploadedMessage fileUploadedMessage;

  public App(FileUploadedMessage fileUploadedMessage) {
    this.fileUploadedMessage = fileUploadedMessage;
  }

  /**
   * Subclasses of this class must implement this method to process business logic.
   *
   * @throws Exception when something goes wrong
   */
  public abstract void process() throws Exception;
}
