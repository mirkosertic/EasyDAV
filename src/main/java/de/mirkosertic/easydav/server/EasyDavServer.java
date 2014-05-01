package de.mirkosertic.easydav.server;

import de.mirkosertic.easydav.crawler.FileSystemCrawler;
import de.mirkosertic.easydav.event.EventManager;
import de.mirkosertic.easydav.fs.UserID;
import de.mirkosertic.easydav.index.ContentExtractor;
import de.mirkosertic.easydav.index.FulltextIndexer;
import de.mirkosertic.easydav.script.ScriptManager;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;

import java.io.File;

public class EasyDavServer {

    public static void main(String[] aArgs) throws Exception {

        EventManager theEventManager = new EventManager();

        ContentExtractor theContentExtractor = new ContentExtractor();
        FulltextIndexer theIndexer = new FulltextIndexer(new File("C:\\Temp2"), theContentExtractor);
        theEventManager.register(theIndexer);

        ConfigurationManager theManager = new ConfigurationManager();

        ScriptManager theScriptManager = new ScriptManager();
        theEventManager.register(theScriptManager);

        Server theWebDavServer = new Server(12000);
        EasyDavServletFactory theServletFactory = new EasyDavServletFactory();
        EasyDavServlet theEasyDavServlet = theServletFactory.create(theManager, theEventManager);
        WebAppContext theWebDavWebApp = new WebAppContext();
        theWebDavWebApp.setContextPath("/");
        theWebDavWebApp.setBaseResource(Resource.newClassPathResource("/webdavwebapp"));
        theWebDavWebApp.setDescriptor("WEB-INF/web.xml");
        theWebDavWebApp.setClassLoader(Server.class.getClassLoader());
        theWebDavWebApp.addServlet(new ServletHolder(theEasyDavServlet), "/");
        theWebDavServer.setHandler(theWebDavWebApp);
        theWebDavServer.start();

        Server theSearchServer = new Server(12001);
        WebAppContext theSearchWebApp = new WebAppContext();
        SearchServlet theSearchServlet = new SearchServlet(theIndexer);
        theSearchWebApp.setContextPath("/");
        theSearchWebApp.setBaseResource(Resource.newClassPathResource("/searchwebapp"));
        theSearchWebApp.setDescriptor("WEB-INF/web.xml");
        theSearchWebApp.setClassLoader(Server.class.getClassLoader());
        theSearchWebApp.addServlet(new ServletHolder(theSearchServlet), "/search");
        theSearchServer.setHandler(theSearchWebApp);
        theSearchServer.start();

        UserID theUserID = UserID.ANONYMOUS;
        FileSystemCrawler theCrawler = new FileSystemCrawler(theEventManager);
        theCrawler.crawl(theUserID, theManager.getConfigurationFor(theUserID).getRootFolder());
    }
}
