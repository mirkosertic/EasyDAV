package de.mirkosertic.easydav.fs;

public class UserID {

    public static final UserID ANONYMOUS = new UserID("anonymous");

    private final String userId;

    public UserID(String aID) {
        userId = aID;
    }

    public String getUserId() {
        return userId;
    }
}
