package de.mirkosertic.easydav.server;

import java.io.File;

import de.mirkosertic.easydav.fs.vfs.VFSProxy;
import de.mirkosertic.easydav.index.ContentExtractor;
import de.mirkosertic.easydav.index.FulltextIndexer;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;

import de.mirkosertic.easydav.crawler.FileSystemCrawler;
import de.mirkosertic.easydav.event.EventManager;
import de.mirkosertic.easydav.fs.VirtualFolder;
import de.mirkosertic.easydav.fs.local.FileProxy;

public class EasyDavServer {

    public static void main(String[] aArgs) throws Exception {

        EventManager theEventManager = new EventManager();

        ContentExtractor theContentExtractor = new ContentExtractor();
        FulltextIndexer theIndexer = new FulltextIndexer(new File("C:\\Temp2"), theContentExtractor);
        theEventManager.register(theIndexer);

        VirtualFolder theRoot = new VirtualFolder("Root");

        FileProxy theTempFiles = new FileProxy(new File("c:\\Temp"), "Temporary Files");
        FileProxy theNetworkData = new FileProxy(new File("U:\\"), "My network share");

        theRoot.add(theTempFiles);
        theRoot.add(theNetworkData);

        FileSystemManager theFileSystemManager = VFS.getManager();
        FileObject theZipFile = theFileSystemManager.resolveFile("jar:C:\\Temp\\migrationdir_001\\sourcedata\\ipgbdta001.zip");
        VFSProxy theZipProxy = new VFSProxy(theZipFile, "VFSZip");
        theRoot.add(theZipProxy);

        Server theWebDavServer = new Server(12000);
        EasyDavServletFactory theServletFactory = new EasyDavServletFactory();
        EasyDavServlet theEasyDavServlet = theServletFactory.create(theRoot, theEventManager);
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

        FileSystemCrawler theCrawler = new FileSystemCrawler(theEventManager);
        theCrawler.crawl(theRoot);
    }
}
