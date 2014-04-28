package de.mirkosertic.easydav.fs.imap;

import de.mirkosertic.easydav.fs.FSFile;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class IMAPFolder implements FSFile {

    private String name;
    private Folder folder;
    private FSFile parent;

    IMAPFolder(Folder aFolder) {
        this(aFolder.getName(), aFolder);
    }

    IMAPFolder(String aName, Folder aFolder) {
        folder = aFolder;
        name = aName;
    }

    public boolean isDirectory() {
        return true;
    }

    public String getName() {
        return name;
    }

    public long lastModified() {
        return 0;
    }

    public long length() {
        return 0;
    }

    public void mkdirs() {
        throw new NotImplementedException("Not implemented");
    }

    public void delete() throws IOException {
        throw new NotImplementedException("Not implemented");
    }

    public boolean exists() {
        return true;
    }

    public boolean renameTo(FSFile aNewFileName) {
        throw new NotImplementedException("Not implemented");
    }

    public OutputStream openWriteStream() throws IOException {
        throw new NotImplementedException("Not implemented");
    }

    public InputStream openInputStream() throws IOException {
        throw new NotImplementedException("Not implemented");
    }

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

    public FSFile asChild(String aResourcePath) {
        int p = aResourcePath.indexOf("/");
        if (p < 0) {
            for (FSFile theFile : listFiles()) {
                if (theFile.getName().equals(aResourcePath)) {
                    return theFile;
                }
            }
            return null;
        }
        String thePrefix = aResourcePath.substring(0, p);
        String theSuffix = aResourcePath.substring(p + 1);
        for (FSFile theFile : listFiles()) {
            if (theFile.getName().equals(thePrefix)) {
                return theFile.asChild(theSuffix);
            }
        }
        return null;
    }

    public FSFile parent() {
        return parent;
    }

    public void setParent(FSFile aParent) {
        parent = aParent;
    }
}
