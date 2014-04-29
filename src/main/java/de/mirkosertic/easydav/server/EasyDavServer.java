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

        Server theServer = new Server(12000);

        EasyDavServletFactory theServletFactory = new EasyDavServletFactory();
        EasyDavServlet theEasyDavServlet = theServletFactory.create(theRoot, theEventManager);

        WebAppContext theWebApp = new WebAppContext();
        theWebApp.setContextPath("/");
        theWebApp.setBaseResource(Resource.newClassPathResource("/webapp"));
        theWebApp.setDescriptor("WEB-INF/web.xml");
        theWebApp.setClassLoader(Server.class.getClassLoader());
        theWebApp.addServlet(new ServletHolder(theEasyDavServlet), "/");

        theServer.setHandler(theWebApp);
        theServer.start();

        FileSystemCrawler theCrawler = new FileSystemCrawler(theEventManager);
        theCrawler.crawl(theRoot);

    }
}
