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
   * Create an compressor input stream from an input stream, autodetecting the compressor type from
   * the first few bytes of the stream. The InputStream must support marks, like
   * BufferedInputStream.
   *
   * @param in the InputStream
   * @return CompressorInputStream object
   */
  public static CompressorInputStream createCompressorInputStream(InputStream in)
      throws CompressorException {
    CompressorStreamFactory compressor = CompressorStreamFactory.getSingleton();
    return in.markSupported()
        ? compressor.createCompressorInputStream(in)
        : compressor.createCompressorInputStream(new BufferedInputStream(in));
  }
  /**
   * Create an archive input stream from an input stream, autodetecting the archive type from the
   * first few bytes of the stream. The InputStream must support marks, like BufferedInputStream.
   *
   * @param in the InputStream
   * @return ArchiveInputStream object
   */
  public static ArchiveInputStream createArchiveInputStream(InputStream in)
      throws ArchiveException {
    ArchiveStreamFactory archiver = new ArchiveStreamFactory();
    return in.markSupported()
        ? archiver.createArchiveInputStream(in)
        : archiver.createArchiveInputStream(new BufferedInputStream(in));
  }
}
