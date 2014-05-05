package de.mirkosertic.easydav.server;

import de.mirkosertic.easydav.fs.RootVirtualFolder;

public class Configuration {

    private final RootVirtualFolder rootFolder;

    Configuration(RootVirtualFolder aRootFolder) {
        rootFolder = aRootFolder;
    }

    public RootVirtualFolder getRootFolder() {
        return rootFolder;
    }
}
