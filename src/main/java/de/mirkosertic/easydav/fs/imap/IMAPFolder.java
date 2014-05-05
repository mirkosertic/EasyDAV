package de.mirkosertic.easydav.fs.imap;

import de.mirkosertic.easydav.fs.FSFile;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

class IMAPFolder implements FSFile {

    private final String name;
    private final Folder folder;
    private FSFile parent;

    IMAPFolder(Folder aFolder) {
        this(aFolder.getName(), aFolder);
    }

    IMAPFolder(String aName, Folder aFolder) {
        folder = aFolder;
        name = aName;
    }

    @Override
    public boolean isDirectory() {
        return true;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public long lastModified() {
        return 0;
    }

    @Override
    public long length() {
        return 0;
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
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public List<FSFile> listFiles() {
        List<FSFile> theFiles = new ArrayList<>();
        try {
            for (Folder theFolder : folder.list()) {
                IMAPFolder theChild = new IMAPFolder(theFolder);
                theChild.setParent(this);
                theFiles.add(theChild);
            }

            // Open the folder if it has a name
            // the default folder has no name and cannot be opened, it does also not contain messages
            if (!StringUtils.isEmpty(folder.getName())) {
                folder.open(Folder.READ_ONLY);

                for (Message theMessage : folder.getMessages()) {
                    IMAPMessage theChild = new IMAPMessage(theMessage);
                    theChild.setParent(this);
                    theFiles.add(theChild);
                }
            }
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        return theFiles;
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
