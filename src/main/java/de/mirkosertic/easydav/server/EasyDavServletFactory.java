package de.mirkosertic.easydav.server;

import org.apache.jackrabbit.webdav.lock.SimpleLockManager;

import de.mirkosertic.easydav.event.EventManager;
import de.mirkosertic.easydav.fs.RootVirtualFolder;

class EasyDavServletFactory {

    EasyDavServlet create(RootVirtualFolder aRootFileSystemFolder, EventManager aEventManager) {
        EasyDavServlet theServlet = new EasyDavServlet();
        theServlet.setDavSessionProvider(new DefaultSavSessionProvider());
        theServlet.setLocatorFactory(new DefaultDavLocatorFactory());
        ResourceFactory theResourceFactory = new ResourceFactory(new SimpleLockManager(), aEventManager);
        theServlet.setResourceFactory(new DefaultDavResourceFactory(aRootFileSystemFolder, theResourceFactory));
        return theServlet;
    }
}
