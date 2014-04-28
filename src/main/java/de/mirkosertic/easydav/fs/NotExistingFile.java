package de.mirkosertic.easydav.fs;

import org.apache.commons.lang3.NotImplementedException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

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
    public void delete() throws IOException {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public OutputStream openWriteStream() throws IOException {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public boolean exists() {
        return false;
    }

    @Override
    public boolean renameTo(FSFile aNewFileName) {
        throw new NotImplementedException("Not implemented");
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