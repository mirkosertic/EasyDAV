package de.mirkosertic.easydav.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;

public class EasyDavServer {

    public static void main(String[] aArgs) throws Exception {
        Server theServer = new Server(12000);

        EasyDavServletFactory theServletFactory = new EasyDavServletFactory();
        EasyDavServlet theEasyDavServlet = theServletFactory.create();

        WebAppContext theWebApp = new WebAppContext();
        theWebApp.setContextPath("/");
        theWebApp.setBaseResource(Resource.newClassPathResource("/webapp"));
        theWebApp.setDescriptor("WEB-INF/web.xml");
        theWebApp.setClassLoader(Server.class.getClassLoader());
         theWebApp.addServlet(new ServletHolder(theEasyDavServlet), "/");

        theServer.setHandler(theWebApp);
        theServer.start();

    }
}
