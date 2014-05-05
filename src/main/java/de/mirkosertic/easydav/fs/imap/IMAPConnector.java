package de.mirkosertic.easydav.fs.imap;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

public class IMAPConnector {

    private final Session session;
    private final Store store;

    IMAPConnector(Session aSession, Store aStore) {
        session = aSession;
        store = aStore;
    }

    public IMAPFolderMount createRootFolder(String aUniqueId, String aName) throws MessagingException {
        return new IMAPFolderMount(aUniqueId, aName, store.getDefaultFolder());
    }
}
