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

    public IMAPFolder createRootFolder(String aName) throws MessagingException {
        return new IMAPFolder(aName, store.getDefaultFolder());
    }
}
