package de.mirkosertic.easydav.fs;

import de.mirkosertic.easydav.event.Event;

public class FileDeletedEvent implements Event {

    private final UserID userId;
    private final FSFile file;

    public FileDeletedEvent(UserID aUserId, FSFile aFile) {
        file = aFile;
        userId = aUserId;
    }

    public FSFile getFile() {
        return file;
    }

    @Override
    public UserID getUserId() {
        return userId;
    }
}
