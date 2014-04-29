package de.mirkosertic.easydav.fs;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.NotImplementedException;

class NotExistingFile implements FSFile {

    private String name;
    private FSFile parent;

    NotExistingFile(String aName, FSFile aParent) {
        name = aName;
        parent = aParent;
    }

    @Override
    public boolean isDirectory() {
        return false;
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
        return false;
    }

    @Override
    public FSFile parent() {
        return parent;
    }

    @Override
    public InputStream openInputStream() throws IOException {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public List<FSFile> listFiles() {
        return new ArrayList<>();
    }

    @Override
    public FSFile asChild(String theResourcePath) {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public void setParent(FSFile aParent) {
        parent = aParent;
    }
}
