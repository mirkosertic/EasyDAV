package de.mirkosertic.easydav.server;

import java.io.File;

import org.aeonbits.owner.ConfigFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;

import de.mirkosertic.easydav.config.ServerConfiguration;
import de.mirkosertic.easydav.crawler.FileSystemCrawler;
import de.mirkosertic.easydav.event.EventManager;
import de.mirkosertic.easydav.fs.UserID;
import de.mirkosertic.easydav.index.ContentExtractor;
import de.mirkosertic.easydav.index.FulltextIndexer;
import de.mirkosertic.easydav.script.ScriptManager;

public class EasyDavServer {

    public static void main(String[] aArgs) throws Exception {

        ServerConfiguration theConfiguration = ConfigFactory
                .create(ServerConfiguration.class,
                        System.getProperties(),
                        System.getenv());

        EventManager theEventManager = new EventManager();

        ContentExtractor theContentExtractor = new ContentExtractor();
        FulltextIndexer theIndexer = new FulltextIndexer(new File(theConfiguration.getIndexDataPath()), theContentExtractor);
        theEventManager.register(theIndexer);

        ConfigurationManager theManager = new ConfigurationManager(theEventManager);

        ScriptManager theScriptManager = new ScriptManager();
        theEventManager.register(theScriptManager);

        Server theWebDavServer = new Server(theConfiguration.getWebDavServerPort());
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

        Server theSearchServer = new Server(theConfiguration.getSearchUIServerPort());
        WebAppContext theSearchWebApp = new WebAppContext();
        SearchServlet theSearchServlet = new SearchServlet(theIndexer, theManager);
        theSearchWebApp.setContextPath("/");
        theSearchWebApp.setBaseResource(Resource.newClassPathResource("/searchwebapp"));
        theSearchWebApp.setDescriptor("WEB-INF/web.xml");
        theSearchWebApp.setClassLoader(Server.class.getClassLoader());
        theSearchWebApp.addServlet(new ServletHolder(theSearchServlet), "/search");
        theSearchServer.setHandler(theSearchWebApp);
        theSearchServer.start();

        UserID theUserID = UserID.ANONYMOUS;
        FileSystemCrawler theCrawler = new FileSystemCrawler(theEventManager);
        theCrawler.crawl(theManager.getConfigurationFor(theUserID).getRootFolder());
    }
}
