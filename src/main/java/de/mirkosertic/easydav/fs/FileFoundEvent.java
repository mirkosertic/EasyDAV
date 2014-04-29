package de.mirkosertic.easydav.fs;

import de.mirkosertic.easydav.event.Event;

public class FileFoundEvent implements Event {

    private final FSFile file;

    public FileFoundEvent(FSFile aFile) {
        file = aFile;
    }

    public FSFile getFile() {
        return file;
    }
}
