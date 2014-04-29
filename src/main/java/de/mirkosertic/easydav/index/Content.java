package de.mirkosertic.easydav.index;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

public class Content {

    private final long fileSize;
    private final long lastModified;
    private final Map<String, String> metadata;
    private final String fileContent;

    public Content(String aFileContent, long fileSize, long lastModified) {
        this.fileSize = fileSize;
        this.lastModified = lastModified;
        metadata = new HashMap<>();
        fileContent = aFileContent;
    }

    public String getFileContent() {
        return fileContent;
    }

    public long getFileSize() {
        return fileSize;
    }

    public long getLastModified() {
        return lastModified;
    }

    public Map<String, String> getMetadata() {
        return Collections.unmodifiableMap(metadata);
    }

    public void addMetaData(String aKey, String aValue) {
        metadata.put(aKey, aValue);
    }
}