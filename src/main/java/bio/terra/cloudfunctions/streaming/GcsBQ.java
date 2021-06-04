package bio.terra.cloudfunctions.streaming;

import bio.terra.cloudevents.GCSEvent;
import com.google.cloud.functions.BackgroundFunction;
import com.google.cloud.functions.Context;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.logging.Logger;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

public class GcsBQ implements BackgroundFunction<GCSEvent> {
  private static final Logger logger = Logger.getLogger(GcsBQ.class.getName());

  @Override
  public void accept(GCSEvent event, Context context) throws Exception {
    logger.info("Event: " + context.eventId());
    logger.info("Event Type: " + context.eventType());
    logger.info(event.toString());

    for (Map.Entry<String, String> entry : System.getenv().entrySet())
      logger.info("Key = " + entry.getKey() + ", Value = " + entry.getValue());

    InputStream in = new URL(event.getMediaLink()).openStream();
    CompressorStreamFactory compressor = CompressorStreamFactory.getSingleton();
    CompressorInputStream uncompressedInputStream =
        in.markSupported()
            ? compressor.createCompressorInputStream(in)
            : compressor.createCompressorInputStream(new BufferedInputStream(in));

    ArchiveStreamFactory archiver = new ArchiveStreamFactory();
    ArchiveInputStream archiveInputStream =
        uncompressedInputStream.markSupported()
            ? archiver.createArchiveInputStream(uncompressedInputStream)
            : archiver.createArchiveInputStream(new BufferedInputStream(uncompressedInputStream));

    // ArchiveInputStream is a special type of InputStream that emits an EOF when it gets to the end
    // of a file in the archive.
    // Once it’s done, call getNextEntry to reset the stream and start reading the next file.
    // When getNextEntry returns null, you’re at the end of the archive.
    ArchiveEntry archiveEntry;
    while ((archiveEntry = archiveInputStream.getNextEntry()) != null) {
      logger.info(archiveEntry.getName() + " " + archiveEntry.getSize() + " bytes");
    }
  }
}
