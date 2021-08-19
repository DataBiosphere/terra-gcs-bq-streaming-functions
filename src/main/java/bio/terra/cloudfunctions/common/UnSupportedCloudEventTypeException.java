package bio.terra.cloudfunctions.common;

/** Thrown to indicate that the Cloud Event Type is not supported. */
public final class UnSupportedCloudEventTypeException extends Exception {
  /** Constructs an UnSupportedCloudEventTypeException with no detail message. */
  public UnSupportedCloudEventTypeException() {}
  /**
   * Constructs an UnSupportedCloudEventTypeException with the specified detail message
   *
   * @param message the detail message
   */
  public UnSupportedCloudEventTypeException(String message) {
    super(message);
  }

  /**
   * Constructs a new exception with the specified detail message and cause. Note that the detail
   * message associated with cause is not automatically incorporated in this exception's detail
   * message.
   *
   * @param message - the detail message (which is saved for later retrieval by the
   *     Throwable.getMessage() method).
   * @param cause - the cause (which is saved for later retrieval by the Throwable.getCause()
   *     method). (A null value is permitted, and indicates that the cause is nonexistent or
   *     unknown.)
   */
  public UnSupportedCloudEventTypeException(String message, Throwable cause) {
    super(message, cause);
  }
}
