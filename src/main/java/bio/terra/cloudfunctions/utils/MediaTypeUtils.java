package bio.terra.cloudfunctions.utils;

import java.io.BufferedInputStream;
import java.io.InputStream;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

public class MediaTypeUtils {
  /**
   * Create a compressor input stream from an input stream, detect the compressor type from the
   * first few bytes of the stream. The InputStream must support marks, like BufferedInputStream.
   *
   * @param in the InputStream
   * @return CompressorInputStream object
   */
  public static CompressorInputStream createCompressorInputStream(InputStream in)
      throws CompressorException {
    CompressorStreamFactory compressor = CompressorStreamFactory.getSingleton();
    return compressor.createCompressorInputStream(markableStream(in));
  }
  /**
   * Create an archive input stream from an input stream, detect the archive type from the first few
   * bytes of the stream. The InputStream must support marks, like BufferedInputStream.
   *
   * @param in the InputStream
   * @return ArchiveInputStream object
   */
  public static ArchiveInputStream createArchiveInputStream(InputStream in)
      throws ArchiveException {
    ArchiveStreamFactory archiver = new ArchiveStreamFactory();
    return archiver.createArchiveInputStream(markableStream(in));
  }

  private static InputStream markableStream(InputStream in) {
    return in.markSupported() ? in : new BufferedInputStream(in);
  }
}
