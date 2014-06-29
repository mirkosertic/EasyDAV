package de.mirkosertic.easydav.fs;

import de.mirkosertic.easydav.event.Event;

public class FolderCreatedEvent implements Event {

    private final FSFile file;

    public FolderCreatedEvent(FSFile aFile) {
        file = aFile;
    }

    public FSFile getFile() {
        return file;
    }
}