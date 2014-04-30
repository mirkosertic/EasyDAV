package de.mirkosertic.easydav.fs;

import java.io.*;
import java.util.List;

public interface FSFile {

    boolean isDirectory();

    String getName();

    long lastModified();

    long length();

    void mkdirs();

    boolean exists();

    InputStream openInputStream() throws IOException;

    List<FSFile> listFiles();

    default String toLocationID() {
        if (parent() != null) {
            return parent().toLocationID() + "/" + getName();
        }
        return getName();
    }

    default FSFile asChild(String aResourcePath) {
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

    FSFile parent();

    void setParent(FSFile aParent);
}
