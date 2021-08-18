package bio.terra.cloudfunctions.common;

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
  protected AppReceiver appReceiver;

  public App(AppReceiver appReceiver) {
    this.appReceiver = appReceiver;
  }

  /**
   * Subclasses of this class must implement this method to process business logic.
   *
   * @throws Exception when something goes wrong
   */
  public abstract void process() throws Exception;
}
