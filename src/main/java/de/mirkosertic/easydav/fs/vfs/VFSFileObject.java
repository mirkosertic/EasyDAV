package de.mirkosertic.easydav.fs.vfs;

import de.mirkosertic.easydav.fs.FSFile;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

class VFSFileObject implements FSFile {

    private final FileObject fileObject;
    private FSFile parent;
    private final String name;

    VFSFileObject(FileObject aFileObject) {
        this(aFileObject, aFileObject.getName().getBaseName());
    }

    VFSFileObject(FileObject aFileObject, String aName) {
        fileObject = aFileObject;
        name = aName;
    }

    @Override
    public boolean isDirectory() {
        try {
            return fileObject.getType() == FileType.FOLDER;
        } catch (FileSystemException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public long lastModified() {
        try {
            return fileObject.getContent().getLastModifiedTime();
        } catch (FileSystemException e) {
            // Dont know whats going on here, so just return 0. Seems to be a problem with the jar provider for instance.
            return 0;
        }
    }

    @Override
    public long length() {
        try {
            return fileObject.getContent().getSize();
        } catch (FileSystemException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void mkdirs() {
        try {
            fileObject.createFolder();
        } catch (FileSystemException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean exists() {
        try {
            return fileObject.exists();
        } catch (FileSystemException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public InputStream openInputStream() throws IOException {
        return fileObject.getContent().getInputStream();
    }

    @Override
    public List<FSFile> listFiles() {
        List<FSFile> theResult = new ArrayList<>();
        try {
            for (FileObject theChild : fileObject.getChildren()) {
                VFSFileObject theChildProxy = new VFSFileObject(theChild);
                theChildProxy.setParent(this);
                theResult.add(theChildProxy);
            }
        } catch (FileSystemException e) {
            throw new RuntimeException(e);
        }
        return theResult;
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