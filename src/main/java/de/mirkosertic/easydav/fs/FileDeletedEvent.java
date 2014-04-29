package de.mirkosertic.easydav.fs;

import de.mirkosertic.easydav.event.Event;

public class FileDeletedEvent implements Event {

    private final FSFile file;

    public FileDeletedEvent(FSFile aFile) {
        file = aFile;
    }

    public FSFile getFile() {
        return file;
    }
}
