package de.mirkosertic.easydav.fs;

public class RootVirtualFolder extends VirtualFolder {

    public RootVirtualFolder() {
        super("");
    }

    @Override
    public String toLocationID() {
        return "";
    }
}
