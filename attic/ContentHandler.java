package bio.terra.cloudfunctions.common;

import com.google.events.cloud.storage.v1.StorageObjectData;

/**
 * This class is an abstract representation of application-specific content handler for extracting
 * relevant information and formatting the StorageObjectData payload for inserting into BigQuery.
 *
 * <p>Sub-classes of this class can be integrated with a DI framework to deploy Cloud Functions like
 * a Service.
 */
public abstract class ContentHandler extends FileTypeDetector {

    public ContentHandler(StorageObjectData storageObjectData) {
        super(storageObjectData);
    }
    /**
     * Subclasses must implement this method as a hook for transforming the raw Storage Object
     * dataStream into domain-specific format before further downstream processing.
     */
    public abstract void translate() throws Exception;
    /**
     * This handleMediaType method calls the base class method then delegates to the domain-specific
     * translator.
     */
    @Override
    public void handleMediaType() throws Exception {
        super.handleMediaType();
        translate();
    }
    /** Subclasses must implement this method as a hook for BigQuery insertion. */
    public abstract void insert() throws Exception;
}
