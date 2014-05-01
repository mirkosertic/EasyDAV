package de.mirkosertic.easydav.fs;

import de.mirkosertic.easydav.event.Event;

public class FileMovedEvent implements Event {

    private final UserID userId;
    private final FSFile source;
    private final FSFile destination;

    public FileMovedEvent(UserID aUserID, FSFile aFile, FSFile aDestination) {
        userId = aUserID;
        source = aFile;
        destination = aDestination;
    }

    public FSFile getSource() {
        return source;
    }

    public FSFile getDestination() {
        return destination;
    }

    @Override
    public UserID getUserId() {
        return userId;
    }
}
