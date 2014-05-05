package de.mirkosertic.easydav.fs.vfs;

import org.apache.commons.vfs2.FileObject;

import de.mirkosertic.easydav.fs.Mount;
import de.mirkosertic.easydav.fs.MountInfo;

public class VFSFileObjectMount extends VFSFileObject implements Mount {

    private final String uniqueId;    

    public VFSFileObjectMount(String aUniqueId, FileObject aFileObject, String aName) {
        super(aFileObject, aName);
        uniqueId = aUniqueId;
    }

    @Override
    public MountInfo getMountInfo() {
        return new MountInfo(uniqueId);
    }
}