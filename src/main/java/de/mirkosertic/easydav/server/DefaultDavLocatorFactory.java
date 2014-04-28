package de.mirkosertic.easydav.server;

import org.apache.jackrabbit.webdav.AbstractLocatorFactory;

public class DefaultDavLocatorFactory extends AbstractLocatorFactory {

    DefaultDavLocatorFactory() {
        super("");
    }

    @Override
    protected String getRepositoryPath(String aResourcePath, String aWorkspacePath) {
        return aResourcePath;
    }

    @Override
    protected String getResourcePath(String aRepositoryPath, String aWorkspacePath) {
        return aRepositoryPath;
    }
}
