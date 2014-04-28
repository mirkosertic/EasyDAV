package de.mirkosertic.easydav.server;

import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.DavSession;

import java.io.File;

public class ResourceFactory {

    public FileDavResource createFileResource(File aFile, DavSession aSession, DavResourceFactory aResourceFactory, DavResourceLocator aResourceLocator) {
        FileDavResource theResource = new FileDavResource(this, aFile, aSession, aResourceFactory, aResourceLocator);
        theResource.initProperties();
        return theResource;
    }

    public FolderDavResource createFolderResource(File aFile, DavSession aSession, DavResourceFactory aResourceFactory, DavResourceLocator aResourceLocator) {
        FolderDavResource theResource = new FolderDavResource(this, aFile, aSession, aResourceFactory, aResourceLocator);
        theResource.initProperties();
        return theResource;
    }

    public DavResource createFileOrFolderResource(File aFile, DavSession aSession, DavResourceFactory aResourceFactory, DavResourceLocator aResourceLocator) {
        if (aFile.isDirectory()) {
            return createFolderResource(aFile, aSession, aResourceFactory, aResourceLocator);
        }
        return createFileResource(aFile, aSession, aResourceFactory, aResourceLocator);
    }
}
