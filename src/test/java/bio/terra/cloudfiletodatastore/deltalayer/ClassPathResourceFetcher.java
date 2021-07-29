package bio.terra.cloudfiletodatastore.deltalayer;

import bio.terra.cloudfiletodatastore.ResourceFetcher;
import java.io.IOException;
import java.io.InputStream;

public class ClassPathResourceFetcher implements ResourceFetcher {

  private final String cpFile;

  public ClassPathResourceFetcher(String cpFile) {
    this.cpFile = cpFile;
  }

  @Override
  public byte[] fetchResourceBytes() {
    try {
      InputStream resourceAsStream =
          ClassLoader.getSystemClassLoader().getResourceAsStream("testfiles/" + cpFile);
      return resourceAsStream.readAllBytes();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
