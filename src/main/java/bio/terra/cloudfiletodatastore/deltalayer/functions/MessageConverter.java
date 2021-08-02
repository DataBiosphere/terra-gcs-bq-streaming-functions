package bio.terra.cloudfiletodatastore.deltalayer.functions;

import bio.terra.cloudfiletodatastore.FileUploadedMessage;
import com.google.events.cloud.storage.v1.StorageObjectData;

public class MessageConverter {

  public static FileUploadedMessage getFileMessage(StorageObjectData toConvert) {
    return new FileUploadedMessage(
        toConvert.getName(),
        toConvert.getBucket(),
        toConvert.getSize(),
        toConvert.getTimeCreated(),
        toConvert.getContentType());
  }
}
