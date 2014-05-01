package de.mirkosertic.easydav.server;

import de.mirkosertic.easydav.event.EventManager;
import org.apache.jackrabbit.webdav.lock.SimpleLockManager;

class EasyDavServletFactory {

    EasyDavServlet create(ConfigurationManager aConfigurationManager, EventManager aEventManager) {
        EasyDavServlet theServlet = new EasyDavServlet();
        theServlet.setDavSessionProvider(new DefaultSavSessionProvider());
        theServlet.setLocatorFactory(new DefaultDavLocatorFactory());
        ResourceFactory theResourceFactory = new ResourceFactory(new SimpleLockManager(), aEventManager);
        theServlet.setResourceFactory(new DefaultDavResourceFactory(aConfigurationManager, theResourceFactory));
        return theServlet;
    }
}
