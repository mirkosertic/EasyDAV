package de.mirkosertic.easydav.server;

import de.mirkosertic.easydav.fs.FSFile;
import de.mirkosertic.easydav.fs.RootVirtualFolder;
import de.mirkosertic.easydav.fs.UserID;
import de.mirkosertic.easydav.fs.local.FileProxy;
import de.mirkosertic.easydav.fs.vfs.VFSProxy;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

public class ConfigurationManager {

    private final FSFile root;

    public ConfigurationManager() throws FileSystemException {
        RootVirtualFolder theRoot = new RootVirtualFolder();
        FileProxy theTempFiles = new FileProxy(new File("c:\\Temp"), "Temporary Files");
        FileProxy theNetworkData = new FileProxy(new File("U:\\"), "My network share");

        theRoot.add(theTempFiles);
        theRoot.add(theNetworkData);

        FileSystemManager theFileSystemManager = VFS.getManager();
        FileObject theZipFile = theFileSystemManager.resolveFile("jar:C:\\Temp\\migrationdir_001\\sourcedata\\ipgbdta001.zip");
        VFSProxy theZipProxy = new VFSProxy(theZipFile, "VFSZip");
        theRoot.add(theZipProxy);

        root = theRoot;
    }

    public Configuration getConfigurationFor(UserID aUserID) {
        return new Configuration(root);
    }

    public Configuration getConfigurationFor(HttpServletRequest aRequest) {
        UserID theID;
        String theUserID = aRequest.getRemoteUser();
        if (StringUtils.isEmpty(theUserID)) {
            theID = UserID.ANONYMOUS;
        } else {
            theID = new UserID(theUserID);
        }
        return getConfigurationFor(theID);
    }
}
