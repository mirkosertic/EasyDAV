package de.mirkosertic.easydav.fs;

public class MountInfo {

    private final String uniqueId;

    public MountInfo(String aUniqueId) {
        uniqueId = aUniqueId;
    }

    public String getUniqueId() {
        return uniqueId;
    }
}