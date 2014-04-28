package de.mirkosertic.easydav.server;

import org.apache.jackrabbit.webdav.DavLocatorFactory;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavSessionProvider;
import org.apache.jackrabbit.webdav.WebdavRequest;
import org.apache.jackrabbit.webdav.server.AbstractWebdavServlet;

public class EasyDavServlet extends AbstractWebdavServlet {

    private DavSessionProvider sessionProvider;
    private DavLocatorFactory locatorFactory;
    private DavResourceFactory resourceFactory;

    @Override
    protected boolean isPreconditionValid(WebdavRequest aRequest, DavResource aDavResource) {
        return true;
    }

    @Override
    public DavSessionProvider getDavSessionProvider() {
        return sessionProvider;
    }

    @Override
    public void setDavSessionProvider(DavSessionProvider aSessionProvider) {
        sessionProvider = aSessionProvider;
    }

    @Override
    public DavLocatorFactory getLocatorFactory() {
        return locatorFactory;
    }

    @Override
    public void setLocatorFactory(DavLocatorFactory aLocatorFactory) {
        locatorFactory = aLocatorFactory;
    }

    @Override
    public DavResourceFactory getResourceFactory() {
        return resourceFactory;
    }

    @Override
    public void setResourceFactory(DavResourceFactory aResourceFactory) {
        resourceFactory = aResourceFactory;
    }
}