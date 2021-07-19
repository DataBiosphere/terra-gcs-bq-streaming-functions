package bio.terra.cloudevents.v1.messagewrapper;

import io.cloudevents.core.data.BytesCloudEventData;

/** A genric class that represents any event message type. */
public abstract class EventMessage<T> extends BytesCloudEventData {
  /**
   * @param value the bytes to wrap
   * @deprecated use {@link BytesCloudEventData#wrap(byte[])}
   */
  @Deprecated
  public EventMessage(byte[] value) {
    super(value);
  }

  /**
   * @param value the bytes to wrap
   * @param mapper functional interface to convert bytes to target T
   */
  public EventMessage(byte[] value, ToTarget<T> mapper) {
    this(value);
    this.mapper = mapper;
  }

  /**
   * Interface defining a conversion from byte array to T. This is similar to {@link
   * java.util.function.Function} but it allows checked exceptions.
   *
   * @param <T> the target type of the conversion
   */
  @FunctionalInterface
  public interface ToTarget<T> {
    /**
     * @param data the byte[] to convert
     * @return the serialized T object.
     * @throws Exception when something goes wrong during the conversion.
     */
    T convert(byte[] data) throws Exception;
  }

  protected T message;
  protected ToTarget<T> mapper;

  public T getMessage() throws Exception {
    return mapper.convert(toBytes());
  }

  public ToTarget<T> getMapper() {
    return mapper;
  }

  public void setMapper(ToTarget<T> mapper) {
    this.mapper = mapper;
  }
}
