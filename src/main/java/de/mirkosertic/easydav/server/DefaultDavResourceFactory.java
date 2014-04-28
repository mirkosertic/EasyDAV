package de.mirkosertic.easydav.server;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.DavServletRequest;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.DavSession;

import java.io.File;

public class DefaultDavResourceFactory implements DavResourceFactory {

    private File rootFile;
    private ResourceFactory resourceFactory;

    public DefaultDavResourceFactory() {
        rootFile = new File("c:\\temp");
        resourceFactory = new ResourceFactory();
    }

    @Override
    public DavResource createResource(DavResourceLocator aLocator, DavServletRequest aRequest, DavServletResponse aResponse) throws DavException {
        if (aLocator.isRootLocation()) {
            return resourceFactory.createFolderResource(rootFile, aRequest.getDavSession(), this, aLocator);
        }
        String theResourcePath = aLocator.getResourcePath();
        File theReference = new File(rootFile, theResourcePath);

        return resourceFactory.createFileOrFolderResource(theReference, aRequest.getDavSession(), this, aLocator);
    }

    @Override
    public DavResource createResource(DavResourceLocator locator, DavSession session) throws DavException {
        throw new NotImplementedException("Not implemented");
    }
}