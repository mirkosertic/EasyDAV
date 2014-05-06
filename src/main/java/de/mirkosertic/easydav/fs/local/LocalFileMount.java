package de.mirkosertic.easydav.fs.local;

import java.io.File;

import de.mirkosertic.easydav.event.EventManager;
import de.mirkosertic.easydav.fs.Mount;
import de.mirkosertic.easydav.fs.MountInfo;

public class LocalFileMount extends LocalFile implements Mount {
    
    private final String uniqueId;

    public LocalFileMount(EventManager aEventManager, String aUniqueId, File aFile, String aDisplayName) {
        super(aEventManager, aFile, aDisplayName);
        uniqueId = aUniqueId;
    }

    @Override
    public MountInfo getMountInfo() {
        return new MountInfo(uniqueId);
    }
}
