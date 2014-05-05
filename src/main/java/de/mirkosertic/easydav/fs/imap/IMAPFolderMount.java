package de.mirkosertic.easydav.fs.imap;

import javax.mail.Folder;

import de.mirkosertic.easydav.fs.Mount;
import de.mirkosertic.easydav.fs.MountInfo;

public class IMAPFolderMount extends IMAPFolder implements Mount {

    private final String uniqueId;

    public IMAPFolderMount(String aUniqueId, String aName, Folder aFolder) {
        super(aName, aFolder);
        uniqueId = aUniqueId;
    }

    @Override
    public MountInfo getMountInfo() {
        return new MountInfo(uniqueId);
    }
}
