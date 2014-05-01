package de.mirkosertic.easydav.server;

import de.mirkosertic.easydav.fs.FSFile;

public class Configuration {

    private final FSFile rootFolder;

    Configuration(FSFile aRootFolder) {
        rootFolder = aRootFolder;
    }

    public FSFile getRootFolder() {
        return rootFolder;
    }
}
