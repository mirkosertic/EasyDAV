package de.mirkosertic.easydav.fs.imap;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import java.util.Properties;

class IMAPConnectorFactory {

    public IMAPConnector create(String aIMAPServer, String aUsername, String aPassword) throws MessagingException {
        Properties theProperties = new Properties();
        theProperties.put("mail.store.protocol","imaps");
        Session theSession = Session.getInstance(theProperties, null);
        Store theStore = theSession.getStore();
        theStore.connect(aIMAPServer, aUsername, aPassword);
        return new IMAPConnector(theSession, theStore);
    }
}
