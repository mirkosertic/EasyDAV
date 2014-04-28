package de.mirkosertic.easydav.server;

import de.mirkosertic.easydav.fs.VirtualFolder;
import de.mirkosertic.easydav.fs.local.FileProxy;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;

import java.io.File;

public class EasyDavServer {

    public static void main(String[] aArgs) throws Exception {

        VirtualFolder theRoot = new VirtualFolder("Root");

        FileProxy theTempFiles = new FileProxy(new File("c:\\Temp"), "Temporary Files");
        FileProxy theNetworkData = new FileProxy(new File("U:\\"), "My network share");

        theRoot.add(theTempFiles);
        theRoot.add(theNetworkData);

        Server theServer = new Server(12000);

        EasyDavServletFactory theServletFactory = new EasyDavServletFactory();
        EasyDavServlet theEasyDavServlet = theServletFactory.create(theRoot);

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
