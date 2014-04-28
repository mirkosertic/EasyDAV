package de.mirkosertic.easydav.server;

import de.mirkosertic.easydav.fs.VirtualFolder;

class EasyDavServletFactory {

    EasyDavServlet create(VirtualFolder aRootFileSystemFolder) {
        EasyDavServlet theServlet = new EasyDavServlet();
        theServlet.setDavSessionProvider(new DefaultSavSessionProvider());
        theServlet.setLocatorFactory(new DefaultDavLocatorFactory());
        theServlet.setResourceFactory(new DefaultDavResourceFactory(aRootFileSystemFolder));
        return theServlet;
    }
}
