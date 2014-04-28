package de.mirkosertic.easydav.fs;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.NotImplementedException;

public class VirtualFolder implements FSFile {

    private String name;
    private FSFile parent;
    private List<FSFile> files;

    public VirtualFolder(String aName) {
        name = aName;
        files = new ArrayList<>();
    }

    public void add(FSFile aFile) {
        files.add(aFile);
        aFile.setParent(this);
    }

    @Override
    public List<FSFile> listFiles() {
        return Collections.unmodifiableList(files);
    }

    @Override
    public FSFile asChild(String aResourcePath) {
        int p = aResourcePath.indexOf("/");
        if (p < 0) {
            for (FSFile theFile : files) {
                if (theFile.getName().equals(aResourcePath)) {
                    return theFile;
                }
            }
            return new NotExistingFile(aResourcePath, this);
        }
        String theSearchFolder = aResourcePath.substring(0, p);
        for (FSFile theFile : files) {
            if (theFile.getName().equals(theSearchFolder)) {
                return theFile.asChild(aResourcePath.substring(p + 1));
            }
        }
        throw new IllegalArgumentException("Cannot find child " + aResourcePath);
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
        return System.currentTimeMillis();
    }

    @Override
    public long length() {
        return 0;
    }

    @Override
    public void mkdirs() {
        throw new NotImplementedException("Cannot physically create virtual folders");
    }

    @Override
    public void delete() throws IOException {
        throw new NotImplementedException("Cannot delete virtual folders");
    }

    @Override
    public OutputStream openWriteStream() throws FileNotFoundException {
        throw new NotImplementedException("Cannot write to virtual folder");
    }

    @Override
    public boolean exists() {
        return true;
    }

    @Override
    public boolean renameTo(FSFile aNewFileName) {
        throw new NotImplementedException("Cannot rename virtual folders");
    }

    @Override
    public FSFile parent() {
        return parent;
    }

    @Override
    public FileInputStream openInputStream() throws FileNotFoundException {
        throw new NotImplementedException("Cannot read from virtual folder");
    }

    @Override
    public void setParent(FSFile aParent) {
        parent = aParent;
    }
}
