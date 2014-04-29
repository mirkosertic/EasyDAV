package de.mirkosertic.easydav.fs;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.NotImplementedException;

public class VirtualFolder implements FSFile {

    private final String name;
    private FSFile parent;
    private final List<FSFile> files;

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
        return null;
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
    public boolean exists() {
        return true;
    }

    @Override
    public FSFile parent() {
        return parent;
    }

    @Override
    public InputStream openInputStream() throws IOException {
        throw new NotImplementedException("Cannot read from virtual folder");
    }

    @Override
    public void setParent(FSFile aParent) {
        parent = aParent;
    }
}
