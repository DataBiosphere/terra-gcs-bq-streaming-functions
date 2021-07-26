package bio.terra.cloudfiletodatastore.deltalayer;

import bio.terra.cloudfiletodatastore.FileBytesHandler;
import bio.terra.cloudfiletodatastore.FileMessage;
import bio.terra.cloudfiletodatastore.MessageProcessor;
import bio.terra.cloudfiletodatastore.ResourceFetcher;
import bio.terra.cloudfiletodatastore.deltalayer.model.PointCorrection;
import java.util.List;
import java.util.logging.Logger;

public class DeltaLayerMessageProcessor extends MessageProcessor {
  private final FileBytesHandler<List<PointCorrection>> fileHandler;
  private final ResourceFetcher resourceFetcher;

  private static final Logger logger = Logger.getLogger(DeltaLayerMessageProcessor.class.getName());

  public DeltaLayerMessageProcessor(FileMessage message) {
    super(message);
    this.resourceFetcher = new GcsFileFetcher(message.getSourceBucket(), message.getResourceUrl());
    this.fileHandler = new FileBytesHandler<>() {};
  }

  @Override
  public void processMessage() {
    byte[] resourceBytes = resourceFetcher.fetchResource();
    List<PointCorrection> pointCorrections = fileHandler.convertBytes(resourceBytes);
    logger.info(String.format("Length of point corrections is %s", pointCorrections.size()));
  }

}
