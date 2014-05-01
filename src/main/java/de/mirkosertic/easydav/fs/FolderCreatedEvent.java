package de.mirkosertic.easydav.fs;

import de.mirkosertic.easydav.event.Event;

public class FolderCreatedEvent implements Event {

    private final UserID userId;
    private final FSFile file;

    public FolderCreatedEvent(UserID aUserID, FSFile aFile) {
        file = aFile;
        userId = aUserID;
    }

    public FSFile getFile() {
        return file;
    }

    @Override
    public UserID getUserId() {
        return userId;
    }
}
