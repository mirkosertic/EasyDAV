package de.mirkosertic.easydav.fs;

import de.mirkosertic.easydav.event.Event;

public class FileMovedEvent implements Event {

    private final FSFile source;
    private final FSFile destination;

    public FileMovedEvent(FSFile aFile, FSFile aDestination) {
        source = aFile;
        destination = aDestination;
    }

    public FSFile getSource() {
        return source;
    }

    public FSFile getDestination() {
        return destination;
    }
}
