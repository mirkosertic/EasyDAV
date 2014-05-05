package de.mirkosertic.easydav.fs;

import java.util.ArrayList;
import java.util.List;

public class RootVirtualFolder extends VirtualFolder {

    public RootVirtualFolder() {
        super("");
    }

    public Mount[] findMountById(String aMountId) {
        List<Mount> theResult = findMountsByIdInternal(aMountId, this, new ArrayList<>());
        return theResult.toArray(new Mount[theResult.size()]);
    }

    private List<Mount> findMountsByIdInternal(String aMountId, FSFile aFile, List<Mount> aMounts) {
        if (aFile instanceof Mount) {
            Mount theMount = (Mount) aFile;
            if (aMountId.equals(theMount.getMountInfo().getUniqueId())) {
                aMounts.add(theMount);
            }
        }
        if (aFile instanceof VirtualFolder) {
            for (FSFile theChild : aFile.listFiles()) {
                findMountsByIdInternal(aMountId, theChild, aMounts);
            }
        }
        return aMounts;
    }
}