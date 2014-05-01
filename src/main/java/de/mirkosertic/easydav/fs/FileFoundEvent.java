package de.mirkosertic.easydav.fs;

import de.mirkosertic.easydav.event.Event;

public class FileFoundEvent implements Event {

    private final FSFile file;
    private final UserID userID;

    public FileFoundEvent(UserID aUserId, FSFile aFile) {
        file = aFile;
        userID = aUserId;
    }

    @Override
    public UserID getUserId() {
        return userID;
    }

    public FSFile getFile() {
        return file;
    }
}
