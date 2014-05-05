package de.mirkosertic.easydav.fs.local;

import java.io.File;

import de.mirkosertic.easydav.fs.Mount;
import de.mirkosertic.easydav.fs.MountInfo;

public class LocalFileMount extends LocalFile implements Mount {
    
    private final String uniqueId;

    public LocalFileMount(String aUniqueId, File aFile, String aDisplayName) {
        super(aFile, aDisplayName);
        uniqueId = aUniqueId;
    }

    @Override
    public MountInfo getMountInfo() {
        return new MountInfo(uniqueId);
    }
}
