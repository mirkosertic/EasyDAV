package de.mirkosertic.easydav.fs.imap;

import de.mirkosertic.easydav.fs.FSFile;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;

import javax.mail.Message;
import javax.mail.MessagingException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class IMAPMessage implements FSFile {

    private final Message message;
    private FSFile parent;

    IMAPMessage(Message aMessage) {
        message = aMessage;
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public String getName() {
        try {
            StringBuilder theResult = new StringBuilder();
            theResult.append("Msg ");
            theResult.append(message.getMessageNumber());
            String theSubject = message.getSubject();
            if (!StringUtils.isEmpty(theSubject)) {
                theResult.append(" ");
                theResult.append(theSubject);
            }
            theResult.append(".elm");
            return theResult.toString();
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long lastModified() {
        try {
            return message.getReceivedDate().getTime();
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long length() {
        try {
            return message.getSize();
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void mkdirs() {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public boolean exists() {
        return true;
    }

    @Override
    public InputStream openInputStream() throws IOException {
        try {
            return message.getInputStream();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<FSFile> listFiles() {
        return new ArrayList<>();
    }

    @Override
    public FSFile asChild(String aResourcePath) {
        return null;
    }

    @Override
    public FSFile parent() {
        return parent;
    }

    @Override
    public void setParent(FSFile aParent) {
        parent = aParent;
    }
}
