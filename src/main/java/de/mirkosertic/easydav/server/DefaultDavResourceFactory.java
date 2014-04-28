package de.mirkosertic.easydav.server;

import de.mirkosertic.easydav.fs.FSFile;
import de.mirkosertic.easydav.fs.VirtualFolder;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.DavServletRequest;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.DavSession;

class DefaultDavResourceFactory implements DavResourceFactory {

    private VirtualFolder rootFile;
    private ResourceFactory resourceFactory;

    public DefaultDavResourceFactory(VirtualFolder aRootFile) {
        rootFile = aRootFile;
        resourceFactory = new ResourceFactory();
    }

    @Override
    public DavResource createResource(DavResourceLocator aLocator, DavServletRequest aRequest, DavServletResponse aResponse) throws DavException {
        if (aLocator.isRootLocation()) {
            return resourceFactory.createFolderResource(rootFile, aRequest.getDavSession(), this, aLocator);
        }

        String theResourcePath = aLocator.getResourcePath();
        if (theResourcePath.startsWith("/")) {
            theResourcePath = theResourcePath.substring(1);
        }
        FSFile theReference = rootFile.asChild(theResourcePath);
        if (theReference == null) {
            throw new DavException(DavServletResponse.SC_NOT_FOUND, "Not found : "+theResourcePath);
        }

        return resourceFactory.createFileOrFolderResource(theReference, aRequest.getDavSession(), this, aLocator);
    }

    @Override
    public DavResource createResource(DavResourceLocator locator, DavSession session) throws DavException {
        throw new NotImplementedException("Not implemented");
    }
}