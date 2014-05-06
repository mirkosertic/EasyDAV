package de.mirkosertic.easydav.fs.local;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import de.mirkosertic.easydav.fs.FileMovedEvent;
import de.mirkosertic.easydav.fs.FolderCreatedEvent;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.NotImplementedException;

import de.mirkosertic.easydav.event.EventManager;
import de.mirkosertic.easydav.fs.Deletable;
import de.mirkosertic.easydav.fs.FSFile;
import de.mirkosertic.easydav.fs.FileDeletedEvent;
import de.mirkosertic.easydav.fs.Renameable;
import de.mirkosertic.easydav.fs.Writeable;

class LocalFile implements FSFile, Deletable, Renameable, Writeable {

    private final String displayName;
    private final File file;
    private final EventManager eventManager;
    FSFile parent;

    LocalFile(EventManager aEventManager, File aFile) {
        this(aEventManager, aFile, aFile.getName());
    }

    LocalFile(EventManager aEventManager, File aFile, String aDisplayName) {
        displayName = aDisplayName;
        file = aFile;
        eventManager = aEventManager;
    }

    @Override
    public boolean isDirectory() {
        return file.isDirectory();
    }

    @Override
    public String getName() {
        return displayName;
    }

    @Override
    public long lastModified() {
        return file.lastModified();
    }

    @Override
    public long length() {
        return file.length();
    }

    @Override
    public void mkdirs() {
        if (file.mkdirs()) {
            eventManager.fire(new FolderCreatedEvent(this));
        }
    }

    @Override
    public void delete() throws IOException {
        FileUtils.forceDelete(file);

        eventManager.fire(new FileDeletedEvent(this));
    }

    @Override
    public OutputStream openWriteStream() throws IOException {
        return new FileOutputStream(file);
    }

    @Override
    public boolean exists() {
        return file.exists();
    }

    @Override
    public boolean renameTo(FSFile aNewFileName) {
        if (!(aNewFileName instanceof LocalFile)) {
            throw new NotImplementedException("Can only rename FileProxies to other FileProxies");
        }
        LocalFile theLocalFile = (LocalFile) aNewFileName;
        if (file.renameTo(theLocalFile.file)) {
            eventManager.fire(new FileMovedEvent(this, theLocalFile));
            return true;
        }
        return false;
    }

    @Override
    public FSFile parent() {
        return parent;
    }

    @Override
    public InputStream openInputStream() throws IOException {
        return new FileInputStream(file);
    }

    @Override
    public List<FSFile> listFiles() {
        List<FSFile> theFiles = new ArrayList<>();
        for (File theFile : file.listFiles()) {
            LocalFile theProxy = new LocalFile(eventManager, theFile);
            theProxy.setParent(this);
            theFiles.add(theProxy);
        }
        return theFiles;
    }

    @Override
    public FSFile asChild(String aResourcePath) {
        LocalFile theProxy = new LocalFile(eventManager, new File(file, aResourcePath));
        theProxy.setParent(this);
        return theProxy;
    }

    @Override
    public void setParent(FSFile aParent) {
        parent = aParent;
    }
}
