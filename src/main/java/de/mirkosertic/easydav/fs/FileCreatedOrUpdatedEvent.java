package de.mirkosertic.easydav.fs;

import de.mirkosertic.easydav.event.Event;

public class FileCreatedOrUpdatedEvent implements Event {

    private final FSFile file;

    public FileCreatedOrUpdatedEvent(FSFile aFile) {
        file = aFile;
    }

    public FSFile getFile() {
        return file;
    }
}