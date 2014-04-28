package de.mirkosertic.easydav.server;

public class EasyDavServletFactory {

    EasyDavServlet create() {
        EasyDavServlet theServlet = new EasyDavServlet();
        theServlet.setDavSessionProvider(new DefaultSavSessionProvider());
        theServlet.setLocatorFactory(new DefaultDavLocatorFactory());
        theServlet.setResourceFactory(new DefaultDavResourceFactory());
        return theServlet;
    }
}
