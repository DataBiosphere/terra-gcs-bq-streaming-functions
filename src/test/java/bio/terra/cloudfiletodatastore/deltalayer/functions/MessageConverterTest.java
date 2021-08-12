package bio.terra.cloudfiletodatastore.deltalayer.functions;

import static junit.framework.TestCase.assertEquals;

import bio.terra.cloudfiletodatastore.FileUploadedMessage;
import com.google.events.cloud.storage.v1.StorageObjectData;
import java.time.OffsetDateTime;
import org.junit.Test;

public class MessageConverterTest {

  @Test
  public void convertMessage() {
    StorageObjectData storageObjectData = new StorageObjectData();
    String bucketName = "my-bucket";
    String fileName = "my-file";
    long size = 100L;
    storageObjectData.setBucket(bucketName);
    storageObjectData.setName(fileName);
    storageObjectData.setSize(size);
    OffsetDateTime timeCreated = OffsetDateTime.now();
    storageObjectData.setTimeCreated(timeCreated);
    FileUploadedMessage fileMessage = MessageConverter.getFileMessage(storageObjectData);
    FileUploadedMessage expected = new FileUploadedMessage(fileName, bucketName, size, timeCreated);
    assertEquals(expected, fileMessage);
  }
}
