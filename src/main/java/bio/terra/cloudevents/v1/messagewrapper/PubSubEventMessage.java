package bio.terra.cloudevents.v1.messagewrapper;

import com.google.events.cloud.pubsub.v1.MessagePublishedData;

public class PubSubEventMessage extends EventMessage<MessagePublishedData> {
  public PubSubEventMessage(byte[] value, ToTarget<MessagePublishedData> mapper) {
    super(value, mapper);
  }
}
